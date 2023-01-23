#include <errno.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <debug.h>
#include <string.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <csapp.h>
#include <exchange.h>
#include <account.h>
#include <protocol.h>
#include <client_registry.h>
#include <server.h>

EXCHANGE *exchange_init();
void exchange_fini(EXCHANGE *xchg);
void exchange_get_status(EXCHANGE *xchg, ACCOUNT *account, BRS_STATUS_INFO *infop);
orderid_t exchange_post_buy(EXCHANGE *xchg, TRADER *trader, quantity_t quantity, funds_t price);
orderid_t exchange_post_sell(EXCHANGE *xchg, TRADER *trader, quantity_t quantity, funds_t price);
int exchange_cancel(EXCHANGE *xchg, TRADER *trader, orderid_t order, quantity_t *quantity);

void *match_making_service();
void remove_all_zero_orders();
void change_bid();
void change_ask();

sem_t marble;
void Sem_init(sem_t *sem, int pshared, unsigned int value);
void P(sem_t *sem);
void V(sem_t *sem);

volatile int exchange_init_bool = 0;
volatile int new_order = 0;
volatile int break_matchmaker = 0;
pthread_t tid;

struct exchange{
    TRADER *trader;
    int type_of_order;      // Buy order is 1, Sell order is -1, 0 is NULL
    orderid_t order_id;
    quantity_t quantity;           // Quantity bought/sold/traded/canceled
    funds_t price;                 // Price to buy product
    funds_t bid;                   // Current highest bid price
    funds_t ask;                   // Current lowest ask price
    funds_t last;                  // Last trade price
    funds_t bal_held;
    quantity_t inv_held;
    struct exchange *sen;
    struct exchange *next;
    struct exchange *prev;
};

/*
 * Initialize a new exchange.
 *
 * @return  the newly initialized exchange, or NULL if initialization failed.
 */
EXCHANGE *exchange_init(){
    if (exchange_init_bool == 1) return NULL;
    Sem_init(&marble,0,1);
    struct exchange *exc_p = malloc(sizeof(struct exchange));
    debug("-----------------------------------exc_p 1 is %p",exc_p);
    if (exc_p == NULL) return NULL;
    struct exchange new_exchange;
    new_exchange.price = 0;
    *exc_p = new_exchange;
    exc_p->trader = NULL;
    exc_p->type_of_order = 0;
    exc_p->order_id = 0;
    exc_p->quantity = -1;
    exc_p->price = 0;
    exc_p->bid = 0;
    exc_p->ask = 0;
    exc_p->last = 0;
    exc_p->bal_held = 0;
    exc_p->inv_held = 0;
    exc_p->sen = exc_p;
    exc_p->next = exc_p;
    exc_p->prev = exc_p;
    exchange_init_bool = 1;
    //init matchmaking thread
    pthread_create(&tid, NULL, match_making_service, NULL);
    return exc_p;
}

/*
 * Finalize an exchange, freeing all associated resources.
 *
 * @param xchg  The exchange to be finalized, which must not
 * be referenced again.
 */
void exchange_fini(EXCHANGE *xchg){
    if (exchange_init_bool == 0) return;
    P(&marble);
    break_matchmaker = 1;
    pthread_join(tid,NULL);
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        debug("Freeing Sen 1 %p",xchg->sen);
        free(xchg->sen);
        V(&marble);
        return;
    } 
    struct exchange *temp = xchg->sen->next;
    struct exchange *hold = temp;
    while (temp != xchg->sen){
        hold = temp->next;
        debug("Freeing temp address at %p",temp);
        trader_unref(temp->trader,"Exchange fini");
        free(temp);
        temp = hold;
    }
    debug("Freeing Sen 2 %p",xchg->sen);
    free(xchg->sen);
    V(&marble);
}

/*
 * Get the current status of the exchange, possibly including the balance and
 * inventory of a specified account.  The values returned are guaranteed to be
 * a consistent snapshot of the state of the exchange at a single point in time,
 * although the state may change as soon as this function has returned.
 *
 * @param xchg  the exchange whose status is to be obtained.
 * @param account  an account whose balance and inventory is to be included with
 * the status, or NULL if no such account is specified.
 * @param infop  pointer to structure to receive the status information.
 * As this structure is designed to be sent in a packet, multibyte fields will be
 * stored in network byte order.
 */
void exchange_get_status(EXCHANGE *xchg, ACCOUNT *account, BRS_STATUS_INFO *infop){
    P(&marble);
    funds_t bal = 0;
    quantity_t inv = 0;
    if (account != NULL){
        BRS_STATUS_INFO infop;
        account_get_status(account,&infop);
        bal = infop.balance;
        inv = infop.inventory;
        debug("The account %p bal is : %d",account,bal);
        debug("The account %p inv is : %d",account,inv);
    }
    infop->balance = bal;
    infop->inventory = inv;
    infop->bid = xchg->sen->bid;
    infop->ask = xchg->sen->ask;
    infop->last = xchg->sen->last;
    infop->orderid = xchg->sen->order_id;
    infop->quantity = xchg->sen->quantity;
    // Should orderid and quantity be here?
    V(&marble);
    return;
}

/*
 * Post a buy order on the exchange on behalf of a trader.
 * The trader is stored with the order, and its reference count is
 * increased by one to account for the stored pointer.
 * Funds equal to the maximum possible cost of the order are
 * encumbered by removing them from the trader's account.
 * A POSTED packet containing details of the order is broadcast
 * to all logged-in traders.
 *
 * @param xchg  The exchange to which the order is to be posted.
 * @param trader  The trader on whose behalf the order is to be posted.
 * @param quantity  The quantity to be bought.
 * @param price  The maximum price to be paid per unit.
 * @return  The order ID assigned to the new order, if successfully posted,
 * otherwise 0.
 */
orderid_t exchange_post_buy(EXCHANGE *xchg, TRADER *trader, quantity_t quantity, funds_t price){
    P(&marble);
    if (exchange_init_bool == 0){
        V(&marble);
        return 0;
    }
    ACCOUNT *acc = trader_get_account(trader);
    BRS_STATUS_INFO infop;
    account_get_status(acc,&infop);
    funds_t bal = infop.balance;
    funds_t total_cost = price * quantity;
    if (total_cost > bal){
        V(&marble);
        return 0;
    }
    // Have to check trader's balance before posting buy
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        struct exchange *exc_p = malloc(sizeof(struct exchange));
        debug("-----------------------------------exc_p 2 is %p",exc_p);
        struct exchange new_exc;
        new_exc.quantity = 0;
        *exc_p = new_exc;
        exc_p->sen = xchg->sen;
        if (price > exc_p->sen->bid){
            exc_p->sen->bid = price;
        }
        exc_p->trader = trader;
        exc_p->type_of_order = 1;
        exc_p->order_id = exc_p->sen->order_id + 1;
        exc_p->sen->order_id++;
        exc_p->quantity = quantity;
        exc_p->price = price;
        // Link List operations
        exc_p->sen->next = exc_p;
        exc_p->sen->prev = exc_p;
        exc_p->next = exc_p->sen;
        exc_p->prev = exc_p->sen;
        // Done with link list operations
        exc_p->bal_held = total_cost;
        exc_p->inv_held = 0;
        account_decrease_balance(acc,total_cost);
        // Have to update trader's balance
        struct brs_packet_header hdr;
        hdr.type = BRS_POSTED_PKT;
        struct brs_notify_info data;
        data.buyer = exc_p->order_id;
        data.price = price;
        data.quantity = quantity;
        data.seller = 0;
        trader_ref(trader,"Trader has placed an order");
        trader_broadcast_packet(&hdr,&data);
        V(&marble);
        debug("ID OF BUY ORDER IS %d",exc_p->order_id);
        return exc_p->order_id;
    }
    struct exchange *exc_p = malloc(sizeof(struct exchange));
    debug("-----------------------------------exc_p 3 is %p",exc_p);
    struct exchange new_exc;
    new_exc.quantity = 0;
    *exc_p = new_exc;
    exc_p->sen = xchg->sen;
    change_ask();
    exc_p->trader = trader;
    exc_p->type_of_order = 1;
    exc_p->order_id = exc_p->sen->order_id + 1;
    exc_p->sen->order_id++;
    exc_p->quantity = quantity;
    exc_p->price = price;
    // Link List operations
    struct exchange *hold = exc_p->sen->next;
    exc_p->sen->next = exc_p;
    hold->prev = exc_p;
    exc_p->next = hold;
    exc_p->prev = exc_p->sen;
    // Done with link list operations
    exc_p->bal_held = total_cost;
    exc_p->inv_held = 0;
    account_decrease_balance(acc,total_cost);
    // Have to update trader's balance
    struct brs_packet_header hdr;
    hdr.type = BRS_POSTED_PKT;
    struct brs_notify_info data;
    data.buyer = exc_p->order_id;
    data.price = price;
    data.quantity = quantity;
    data.seller = 0;
    trader_ref(trader,"Trader has placed an order");
    trader_broadcast_packet(&hdr,&data);
    new_order = 1;
    V(&marble);
    debug("ID OF BUY ORDER IS %d",exc_p->order_id);
    return exc_p->order_id;
}

/*
 * Post a sell order on the exchange on behalf of a trader.
 * The trader is stored with the order, and its reference count is
 * increased by one to account for the stored pointer.
 * Inventory equal to the amount of the order is
 * encumbered by removing it from the trader's account.
 * A POSTED packet containing details of the order is broadcast
 * to all logged-in traders.
 *
 * @param xchg  The exchange to which the order is to be posted.
 * @param trader  The trader on whose behalf the order is to be posted.
 * @param quantity  The quantity to be sold.
 * @param price  The minimum sale price per unit.
 * @return  The order ID assigned to the new order, if successfully posted,
 * otherwise 0.
 */
orderid_t exchange_post_sell(EXCHANGE *xchg, TRADER *trader, quantity_t quantity, funds_t price){
    P(&marble);
    if (exchange_init_bool == 0){
        V(&marble);
        return 0;
    }
    ACCOUNT *acc = trader_get_account(trader);
    BRS_STATUS_INFO infop;
    account_get_status(acc,&infop);
    quantity_t inv = infop.inventory;
    if (quantity > inv){
        V(&marble);
        return 0;
    }
    // Have to check trader's balance before posting sell
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        struct exchange *exc_p = malloc(sizeof(struct exchange));
        debug("-----------------------------------exc_p is 4 %p",exc_p);
        struct exchange new_exc;
        new_exc.quantity = 0;
        *exc_p = new_exc;
        exc_p->sen = xchg->sen;
        xchg->sen->ask = price;
        exc_p->trader = trader;
        exc_p->type_of_order = -1;
        exc_p->order_id = exc_p->sen->order_id + 1;
        exc_p->sen->order_id++;
        exc_p->quantity = quantity;
        exc_p->price = price;
        // Link List operations
        exc_p->sen->next = exc_p;
        exc_p->sen->prev = exc_p;
        exc_p->next = exc_p->sen;
        exc_p->prev = exc_p->sen;
        // Done with link list operations
        exc_p->inv_held = quantity;
        exc_p->bal_held = 0;
        account_decrease_inventory(acc,quantity);
        // Have to update trader's balance
        struct brs_packet_header hdr;
        hdr.type = BRS_POSTED_PKT;
        struct brs_notify_info data;
        data.buyer = 0;
        data.price = price;
        data.quantity = quantity;
        data.seller = exc_p->order_id;
        trader_ref(trader,"Trader has placed an order");
        trader_broadcast_packet(&hdr,&data);
        V(&marble);
        debug("ID OF SELL ORDER IS %d",exc_p->order_id);
        return exc_p->order_id;
    }
    struct exchange *exc_p = malloc(sizeof(struct exchange));
    debug("-----------------------------------exc_p is 5 %p",exc_p);
    struct exchange new_exc;
    new_exc.quantity = 0;
    *exc_p = new_exc;
    exc_p->sen = xchg->sen;
    if (price < exc_p->sen->ask){
        exc_p->sen->ask = price;
    }
    exc_p->trader = trader;
    exc_p->type_of_order = -1;
    exc_p->order_id = exc_p->sen->order_id + 1;
    exc_p->sen->order_id++;
    exc_p->quantity = quantity;
    exc_p->price = price;
    // Link List operations
    struct exchange *hold = exc_p->sen->next;
    exc_p->sen->next = exc_p;
    hold->prev = exc_p;
    exc_p->next = hold;
    exc_p->prev = exc_p->sen;
    // Done with link list operations
    exc_p->inv_held = quantity;
    exc_p->bal_held = 0;
    account_decrease_inventory(acc,quantity);
    // Have to update trader's balance
    struct brs_packet_header hdr;
    hdr.type = BRS_POSTED_PKT;
    struct brs_notify_info data;
    data.buyer = 0;
    data.price = price;
    data.quantity = quantity;
    data.seller = exc_p->order_id;
    trader_ref(trader,"Trader has placed an order");
    trader_broadcast_packet(&hdr,&data);
    V(&marble);
    debug("ID OF SELL ORDER IS %d",exc_p->order_id);
    new_order = 1;
    return exc_p->order_id;
}

/*
 * Attempt to cancel a pending order.
 * If successful, the quantity of the canceled order is returned in a variable,
 * and a CANCELED packet containing details of the canceled order is
 * broadcast to all logged-in traders.
 *
 * @param xchg  The exchange from which the order is to be cancelled.
 * @param trader  The trader cancelling the order is to be posted,
 * which must be the same as the trader who originally posted the order.
 * @param id  The order ID of the order to be cancelled.
 * @param quantity  Pointer to a variable in which to return the quantity
 * of the order that was canceled.  Note that this need not be the same as
 * the original order amount, as the order could have been partially
 * fulfilled by trades.
 * @return  0 if the order was successfully cancelled, -1 otherwise.
 * Note that cancellation might fail if a trade fulfills and removes the
 * order before this function attempts to cancel it.
 */
int exchange_cancel(EXCHANGE *xchg, TRADER *trader, orderid_t order, quantity_t *quantity){
    P(&marble);
    if (exchange_init_bool == 0){
        V(&marble);
        return -1;
    }
    ACCOUNT *acc = trader_get_account(trader);
    struct exchange *temp = xchg->sen->next;
    struct exchange *hold = temp;
    while (temp != xchg->sen){
        hold = temp->next;
        if (temp->order_id == order){
            if (trader != temp->trader){
                V(&marble);
                return -1;
            }
            if (temp->type_of_order == 1){
                account_increase_balance(acc,temp->bal_held);
            }
            else if (temp->type_of_order == -1){
                account_increase_inventory(acc,temp->inv_held);
            }
            // GIVE moey and inv back to account
            struct exchange *e_next = temp->next;
            struct exchange *e_prev = temp->prev;
            e_prev->next = e_next;
            e_next->prev = e_prev;
            *quantity = temp->quantity;
            struct brs_packet_header hdr;
            hdr.type = BRS_CANCELED_PKT;
            struct brs_notify_info data;
            int type_of_order = temp->type_of_order;
            if (type_of_order == -1){
                data.seller = temp->order_id;
                data.buyer = 0;
            } 
            if (type_of_order == 1){
                data.buyer = temp->order_id;
                data.seller = 0;
            } 
            data.price = temp->bal_held;
            data.quantity = temp->quantity;
            debug("data.buyer equal to : %d",temp->order_id);
            debug("data.seller equal to : %d",temp->order_id);
            debug("data.quantity equal to : %d",temp->bal_held);
            debug("data.price equal to : %d",temp->quantity);
            trader_unref(trader,"Trader has removed an order");
            trader_broadcast_packet(&hdr,&data);
            free(temp);
            V(&marble);
            return 0;
        }
        temp = hold;
    }
    V(&marble);
    return -1;
}

void *match_making_service(){
    while (1){
        if (break_matchmaker == 1){
            return NULL;
        }
        if (new_order == 1){
            P(&marble);
            // Looping orders
            EXCHANGE *xchg = exchange->sen;
            struct exchange *temp_single_order = xchg->sen->next;
            struct exchange *temp_all_orders = xchg->sen->next;
            while (temp_single_order != xchg->sen){

                while (temp_all_orders != xchg->sen){
                    if (((temp_single_order->type_of_order == 1) && (temp_all_orders->type_of_order == -1)
                        && (temp_single_order->price >= temp_all_orders->price))){
                            quantity_t amt_exh = 0;
                            funds_t buyer_price_hold = temp_single_order->price;
                            funds_t seller_price_hold = temp_all_orders->price;
                            if ((xchg->last >= seller_price_hold) && (xchg->last <= buyer_price_hold)){
                                temp_single_order->price = xchg->last;
                                temp_all_orders->price = xchg->last;
                            }
                            else {
                                int b_minus_lastTrade = temp_single_order->price - xchg->last;
                                int s_minus_lastTrade = temp_all_orders->price - xchg->last;
                                if (b_minus_lastTrade < 0) b_minus_lastTrade = (b_minus_lastTrade * -1);
                                if (s_minus_lastTrade < 0) s_minus_lastTrade = (s_minus_lastTrade * -1);
                                if (b_minus_lastTrade == s_minus_lastTrade){
                                    temp_single_order->price = temp_single_order->price;
                                    temp_all_orders->price = temp_single_order->price;
                                }
                                else if (b_minus_lastTrade > s_minus_lastTrade ){
                                    temp_single_order->price = temp_all_orders->price;
                                    temp_all_orders->price = temp_all_orders->price;
                                }
                                else if (b_minus_lastTrade < s_minus_lastTrade ){
                                    temp_single_order->price = temp_single_order->price;
                                    temp_all_orders->price = temp_single_order->price;
                                }
                            }
                            ACCOUNT *acc_buyer = trader_get_account(temp_single_order->trader);
                            ACCOUNT *acc_seller = trader_get_account(temp_all_orders->trader);
                            if (temp_single_order->quantity == temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_single_order->quantity);
                                account_increase_balance(acc_seller,temp_all_orders->quantity * temp_all_orders->price);
                                temp_all_orders->bal_held = 0;
                                temp_all_orders->inv_held = 0;
                                temp_single_order->bal_held = 0;
                                temp_single_order->inv_held = 0;
                                amt_exh = temp_single_order->quantity;
                                temp_single_order->quantity = 0;
                                temp_all_orders->quantity = 0;
                            }
                            else if (temp_single_order->quantity > temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_all_orders->quantity);
                                quantity_t inv_left = temp_single_order->quantity - temp_all_orders->quantity;
                                account_increase_balance(acc_seller,temp_all_orders->quantity * temp_all_orders->price);
                                temp_single_order->bal_held = temp_single_order->bal_held - (temp_all_orders->quantity * temp_all_orders->price);
                                temp_all_orders->inv_held = 0;
                                amt_exh = temp_all_orders->quantity;
                                temp_single_order->quantity = inv_left;
                                temp_all_orders->quantity = 0;
                            }
                            else if (temp_single_order->quantity < temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_single_order->quantity);
                                quantity_t inv_left = temp_all_orders->quantity - temp_single_order->quantity;
                                account_increase_balance(acc_seller,temp_single_order->quantity * temp_all_orders->price);
                                temp_all_orders->inv_held = temp_all_orders->inv_held - temp_single_order->quantity;
                                temp_single_order->bal_held = 0;
                                amt_exh = temp_single_order->quantity;
                                temp_single_order->quantity = 0;
                                temp_all_orders->quantity = inv_left;
                            }
                            funds_t price_traded = temp_all_orders->price;
                            xchg->sen->last = temp_all_orders->price;
                            temp_single_order->price = buyer_price_hold;
                            temp_all_orders->price = seller_price_hold;
                            change_ask();
                            change_bid();
                            struct brs_packet_header hdr_b;
                            hdr_b.type = BRS_BOUGHT_PKT;
                            struct brs_packet_header hdr_s;
                            hdr_s.type = BRS_SOLD_PKT;
                            struct brs_packet_header hdr_t;
                            hdr_t.type = BRS_TRADED_PKT;
                            struct brs_notify_info data;
                            data.buyer = temp_single_order->order_id;
                            data.seller = temp_all_orders->order_id;
                            data.quantity = amt_exh;
                            data.price = price_traded;
                            trader_send_packet(temp_single_order->trader,&hdr_b,&data);
                            trader_send_packet(temp_all_orders->trader,&hdr_s,&data);
                            trader_broadcast_packet(&hdr_t,&data);
                            debug("data.buyer equal to : %d",temp_single_order->order_id);
                            debug("data.seller equal to : %d",temp_all_orders->order_id);
                            debug("data.quantity equal to : %d",amt_exh);
                            debug("data.price equal to : %d",price_traded);
                            remove_all_zero_orders();
                    }
                    else if ((temp_single_order->type_of_order == -1) && (temp_all_orders->type_of_order == 1)
                        && (temp_single_order->price <= temp_all_orders->price)){
                            quantity_t amt_exh = 0;
                            funds_t buyer_price_hold = temp_all_orders->price;
                            funds_t seller_price_hold = temp_single_order->price;
                            if ((xchg->last >= seller_price_hold) && (xchg->last <= buyer_price_hold)){
                                temp_single_order->price = xchg->last;
                                temp_all_orders->price = xchg->last;
                            }
                            else {
                                int s_minus_lastTrade = temp_single_order->price - xchg->last;
                                int b_minus_lastTrade = temp_all_orders->price - xchg->last;
                                if (b_minus_lastTrade < 0) b_minus_lastTrade = (b_minus_lastTrade * -1);
                                if (s_minus_lastTrade < 0) s_minus_lastTrade = (s_minus_lastTrade * -1);
                                if (b_minus_lastTrade == s_minus_lastTrade){
                                    temp_single_order->price = temp_single_order->price;
                                    temp_all_orders->price = temp_single_order->price;
                                }
                                else if (b_minus_lastTrade > s_minus_lastTrade ){
                                    temp_single_order->price = temp_single_order->price;
                                    temp_all_orders->price = temp_single_order->price;
                                }
                                else if (b_minus_lastTrade < s_minus_lastTrade ){
                                    temp_single_order->price = temp_all_orders->price;
                                    temp_all_orders->price = temp_all_orders->price;
                                }
                            }
                            ACCOUNT *acc_buyer = trader_get_account(temp_all_orders->trader);
                            ACCOUNT *acc_seller = trader_get_account(temp_single_order->trader);
                            if (temp_single_order->quantity == temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_single_order->quantity);
                                account_increase_balance(acc_seller,temp_all_orders->quantity * temp_all_orders->price);
                                temp_all_orders->bal_held = 0;
                                temp_all_orders->inv_held = 0;
                                temp_single_order->bal_held = 0;
                                temp_single_order->inv_held = 0;
                                amt_exh = temp_all_orders->quantity;
                                temp_single_order->quantity = 0;
                                temp_all_orders->quantity = 0;
                            }
                            else if (temp_single_order->quantity > temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_all_orders->quantity);
                                quantity_t inv_left = temp_single_order->quantity - temp_all_orders->quantity;
                                account_increase_balance(acc_seller,temp_all_orders->quantity * temp_single_order->price);
                                temp_single_order->inv_held = temp_single_order->inv_held - temp_all_orders->quantity;
                                temp_all_orders->bal_held = 0;
                                amt_exh = temp_all_orders->quantity;
                                temp_single_order->quantity = inv_left;
                                temp_all_orders->quantity = 0;
                            }
                            else if (temp_single_order->quantity < temp_all_orders->quantity){
                                account_increase_inventory(acc_buyer,temp_single_order->quantity);
                                quantity_t inv_left = temp_all_orders->quantity - temp_single_order->quantity;
                                account_increase_balance(acc_seller,temp_single_order->quantity * temp_single_order->price);
                                temp_all_orders->bal_held = temp_all_orders->bal_held - (temp_single_order->quantity * temp_single_order->price);
                                temp_single_order->inv_held = 0;
                                amt_exh = temp_single_order->quantity;
                                temp_single_order->quantity = 0;
                                temp_all_orders->quantity = inv_left;
                            }
                            funds_t price_traded = temp_single_order->price;
                            xchg->sen->last = temp_single_order->price;
                            temp_single_order->price = seller_price_hold;
                            temp_all_orders->price = buyer_price_hold;
                            change_ask();
                            change_bid();
                            struct brs_packet_header hdr_b;
                            hdr_b.type = BRS_BOUGHT_PKT;
                            struct brs_packet_header hdr_s;
                            hdr_s.type = BRS_SOLD_PKT;
                            struct brs_packet_header hdr_t;
                            hdr_t.type = BRS_TRADED_PKT;
                            struct brs_notify_info data;
                            data.buyer = temp_all_orders->order_id;
                            data.seller = temp_single_order->order_id;
                            data.quantity = amt_exh;
                            data.price = price_traded;
                            trader_send_packet(temp_all_orders->trader,&hdr_b,&data);
                            trader_send_packet(temp_single_order->trader,&hdr_s,&data);
                            trader_broadcast_packet(&hdr_t,&data);
                            debug("data.buyer equal to : %d",temp_all_orders->order_id);
                            debug("data.seller equal to : %d",temp_single_order->order_id);
                            debug("data.quantity equal to : %d",amt_exh);
                            debug("data.price equal to : %d",price_traded);
                            remove_all_zero_orders();
                    }

                    temp_all_orders = temp_all_orders->next;
                }

                temp_single_order = temp_single_order->next;
            }
            // Done looping orders
            new_order = 0;
            V(&marble);

        }
    }
}

void remove_all_zero_orders(){
    EXCHANGE *xchg = exchange->sen;
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        debug("No zero quantity orders to be removed");
        V(&marble);
        return;
    } 
    struct exchange *temp = xchg->sen->next;
    struct exchange *hold = temp;
    while (temp != xchg->sen){
        hold = temp->next;
        if (temp->quantity == 0){
            struct exchange *temp_next = temp->next;
            struct exchange *temp_prev = temp->prev;
            temp_next->prev = temp_prev;
            temp_prev->next = temp_next;
            debug("BAL HELD IS AT QUANT 0 %d",temp->bal_held);
            debug("TEMP HELD IS AT QUANT 0 %d",temp->inv_held);
            if (temp->bal_held > 0) account_increase_balance(trader_get_account(temp->trader),temp->bal_held);
            if (temp->inv_held > 0) account_increase_inventory(trader_get_account(temp->trader),temp->inv_held);
            trader_unref(temp->trader,"Quantity is 0");
            free(temp);
        }
        temp = hold;
    }
}

void change_bid(){
    EXCHANGE *xchg = exchange->sen;
    funds_t bid = xchg->sen->bid;
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        V(&marble);
        return;
    }
    struct exchange *temp = xchg->sen->next;
    while (temp != xchg->sen){
        if (temp->type_of_order == 1){
            if (temp->price > bid){
                xchg->sen->bid = temp->price;
            }
        }
        temp = temp->next;
        bid = xchg->sen->bid;
    }
    return;
}

void change_ask(){
    EXCHANGE *xchg = exchange->sen;
    funds_t ask = xchg->ask;
    if ((xchg->sen->next == xchg->sen) && (xchg->sen->prev == xchg->sen)){
        V(&marble);
        return;
    }
    struct exchange *temp = xchg->sen->next;
    while (temp != xchg->sen){
        if (temp->type_of_order == -1){
            if (xchg->sen->ask == 0){
                xchg->sen->ask = temp->price;
            }
            if (temp->price < ask){
                xchg->sen->ask = temp->price;
            }
        }
        temp = temp->next;
        ask = xchg->ask;
    }
    return;
}