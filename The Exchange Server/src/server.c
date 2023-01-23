#define _XOPEN_SOURCE 700
#include <server.h>
#include <protocol.h>
#include <errno.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <debug.h>
#include <string.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <exchange.h>
#include <signal.h>
#include <time.h>
#include <pthread.h>

void set_secAndNsec(BRS_PACKET_HEADER *hdr);


void *brs_client_service(void *arg);

/*
 * Thread function for the thread that handles a particular client.
 *
 * @param  Pointer to a variable that holds the file descriptor for
 * the client connection.  This pointer must be freed once the file
 * descriptor has been retrieved.
 * @return  NULL
 *
 * This function executes a "service loop" that receives packets from
 * the client and dispatches to appropriate functions to carry out
 * the client's requests.  It also maintains information about whether
 * the client has logged in or not.  Until the client has logged in,
 * only LOGIN packets will be honored.  Once a client has logged in,
 * LOGIN packets will no longer be honored, but other packets will be.
 * The service loop ends when the network connection shuts down and
 * EOF is seen.  This could occur either as a result of the client
 * explicitly closing the connection, a timeout in the network causing
 * the connection to be closed, or the main thread of the server shutting
 * down the connection as part of graceful termination.
 */
void *brs_client_service(void *arg){  
    pthread_detach(pthread_self());
    int connfd = *((int *)arg);
    free(arg);
    creg_register(client_registry,connfd);
    debug("[%d] Starting client service",connfd);
    int login_bool = 0;
    BRS_PACKET_HEADER ack;
    ack.type = BRS_ACK_PKT;
    BRS_PACKET_HEADER nack;
    nack.type = BRS_NACK_PKT;
    TRADER *client_p;
    ACCOUNT *new_acc = NULL;
    BRS_PACKET_HEADER hdr;
    void *payload = NULL;
    while (1){
        //debug("-----------IT SHOULD PRINT THIS");
        int res = proto_recv_packet(connfd, &hdr, &payload);
        //debug("--------------HDR TPYE IS %d",hdr.type);
        if (res == -1){
            if (login_bool == 0){
                if (payload != NULL) free(payload);
                payload = NULL;
                creg_unregister(client_registry,connfd);
                return NULL;
            }
            debug("proto_recv_packet failed");
            if (payload != NULL) free(payload);
            payload = NULL;
            creg_unregister(client_registry,connfd);
            trader_logout(client_p);
            return NULL;
        }
        switch (hdr.type)
        {
        case (BRS_LOGIN_PKT):
            debug("[%d] Login packet received",connfd);
            if (login_bool == 1){
                debug("Someone already login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            int size = ntohs(hdr.size);
            char *str_nterm = malloc(size+1);
            char *str_helper = str_nterm;
            memcpy(str_nterm,(char*)payload,size);
            int count = 0;
            while (count < size){
                str_nterm++;
                count++;
            }
            *str_nterm = '\0';
            client_p = trader_login(connfd,str_helper);
            new_acc = trader_get_account(client_p);
            free(str_helper);
            if (new_acc == NULL){
                if (payload != NULL) free(payload);
                payload = NULL;
                creg_unregister(client_registry,connfd);
                return NULL;
            } 
            login_bool = 1;
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,NULL);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_STATUS_PKT):
            debug("[%d] Status packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_status_info status_info;
            exchange_get_status(exchange,new_acc,&status_info);
            
            debug("balance: %d, inventory: %d, bid: %d, ask: %d, last: %d, order: %d",status_info.balance,status_info.inventory,status_info.bid,
            status_info.ask,status_info.last,status_info.orderid);
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,&status_info);
            break;
        case (BRS_DEPOSIT_PKT):
            debug("[%d] Deposit packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_funds_info *f_info = payload;
            int dep_amt = ntohl(f_info->amount);
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,&dep_amt);
            account_increase_balance(new_acc,dep_amt);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_WITHDRAW_PKT):
            debug("[%d] Withdraw packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_funds_info *w_info = payload;
            int w_amt = ntohl(w_info->amount);
            int withdraw_res = account_decrease_balance(new_acc,w_amt);
            if (withdraw_res == -1){
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,payload);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            } 
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,&w_amt); 
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_ESCROW_PKT):
            debug("[%d] Escrow packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_escrow_info *e_info = payload;
            int e_amt = ntohl(e_info->quantity);
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,&e_amt);
            account_increase_inventory(new_acc,e_amt);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_RELEASE_PKT):
            debug("[%d] Release packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_escrow_info *r_info = payload;
            int r_amt = ntohl(r_info->quantity);
            int rele_info = account_decrease_inventory(new_acc,r_amt);
            if (rele_info == -1){
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,payload);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,&r_amt);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_BUY_PKT):
            debug("[%d] Buy packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_order_info *buy_info = payload;
            int buy_price = ntohl(buy_info->price);
            int buy_quantity = ntohl(buy_info->quantity);
            orderid_t buy_order_id = exchange_post_buy(exchange,client_p,buy_quantity,buy_price);
            if (buy_order_id == 0){
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,payload);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_status_info *buy_stat_info = payload;
            buy_stat_info->orderid = buy_order_id;
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,buy_stat_info);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_SELL_PKT):
            debug("[%d] Sell packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_order_info *sell_info = payload;
            int sell_price = ntohl(sell_info->price);
            int sell_quantity = ntohl(sell_info->quantity);
            orderid_t sell_order_id = exchange_post_sell(exchange,client_p,sell_quantity,sell_price);
            if (sell_order_id == 0){
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,payload);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_status_info *sell_stat_info = payload;
            sell_stat_info->orderid = sell_order_id;
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,sell_stat_info);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;
        case (BRS_CANCEL_PKT):
            debug("[%d] Cancel packet received",connfd);
            if (login_bool == 0){
                debug("Someone has to login");
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,NULL);   
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_cancel_info *cancel_info = payload;
            orderid_t cancel_ord = ntohl(cancel_info->order);
            quantity_t cancel_quant = 0;
            int cancel_res = exchange_cancel(exchange,client_p,cancel_ord,&cancel_quant);
            if (cancel_res == -1){
                set_secAndNsec(&nack);
                proto_send_packet(connfd,&nack,payload);
                if (payload != NULL) free(payload);
                payload = NULL;
                break;
            }
            struct brs_status_info *cancel_stat_info = payload;
            cancel_stat_info->orderid = cancel_ord;
            cancel_stat_info->quantity = cancel_quant;
            set_secAndNsec(&ack);
            proto_send_packet(connfd,&ack,cancel_stat_info);
            if (payload != NULL) free(payload);
            payload = NULL;
            break;   
        default:
            debug("???");
            if (payload != NULL) free(payload);
            payload = NULL;
            //break;
        }
    }
    if (payload != NULL) free(payload);
    payload = NULL;
    creg_unregister(client_registry,connfd);
    return NULL;
}

// new_acc = account_lookup(client_name); another debug statment not sure if needed
// How to use account_get_status(new_acc,payload);

void set_secAndNsec(BRS_PACKET_HEADER *hdr){
    struct timespec tp;
    clock_gettime(CLOCK_REALTIME,&tp);
    hdr->timestamp_sec = tp.tv_sec;
    hdr->timestamp_nsec = tp.tv_nsec;
    debug("Seconds is %ld",tp.tv_sec);
    debug("NanoSec is %ld",tp.tv_nsec);
}