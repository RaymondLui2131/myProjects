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
#include <account.h>
#include <csapp.h>

sem_t marble1;
void Sem_init(sem_t *sem, int pshared, unsigned int value);
void P(sem_t *sem);
void V(sem_t *sem);

int accounts_init(void);
void accounts_fini(void);
ACCOUNT *account_lookup(char *name);
void account_increase_balance(ACCOUNT *account, funds_t amount);
int account_decrease_balance(ACCOUNT *account, funds_t amount);
void account_increase_inventory(ACCOUNT *account, quantity_t quantity);
int account_decrease_inventory(ACCOUNT *account, quantity_t quantity);
void account_get_status(ACCOUNT *account, BRS_STATUS_INFO *infop);

ACCOUNT *acc_sen = NULL;
int acc_init_bool = 0;

struct account{
    int number_of_accounts;
    int balance;
    int inventory;
    char *login_name;
    struct account *sen;
    struct account *next;
    struct account *prev;
};

/*
 * Initialize the accounts module.
 * This must be called before doing calling any other functions in this
 * module.
 *
 * @return 0 if initialization succeeds, -1 otherwise.
 */
int accounts_init(void){
    if (acc_init_bool == 1) return -1;
    Sem_init(&marble1,0,1);
    struct account *acc_p = malloc(sizeof(struct account));
    if (acc_p == NULL) return -1;
    struct account new_account;
    new_account.balance = 0;
    *acc_p = new_account;
    acc_p->number_of_accounts = 0;
    acc_p->balance = 0;
    acc_p->inventory = 0;
    acc_p->login_name = NULL;
    acc_p->next = acc_p;
    acc_p->prev = acc_p;
    acc_p->sen = acc_p;
    acc_sen = acc_p;
    acc_init_bool = 1;
    return 0;
}
          
/*
 * Finalize the accounts module, freeing all associated resources.
 * This should be called when the accounts module is no longer required.
 */
void accounts_fini(void){
    if (acc_init_bool == 0) return;
    P(&marble1);
    if ((acc_sen->sen->next == acc_sen->sen) && (acc_sen->sen->prev == acc_sen->sen)){
        debug("Freeing Sen %p",acc_sen->sen);
        free(acc_sen->sen);
        V(&marble1);
        return;
    } 
    struct account *temp = acc_sen->sen->next;
    struct account *hold = temp;
    while (temp != acc_sen->sen){
        hold = temp->next;
        free(temp->login_name);
        free(temp);
        temp = hold;
    }
    debug("Freeing Sen");
    free(acc_sen->sen);
    V(&marble1);
}

/*
 * Look up an account for a specified user name.  If an account with
 * the specified name already exists, that account is returned, otherwise
 * a new account with zero balance and inventory is created.
 * An account, once created for a particular user name, persists until
 * the server is shut down.
 *
 * @param name  The user name, which is copied by this function.
 * @return A pointer to an ACCOUNT object, in case of success, otherwise NULL.
 */
ACCOUNT *account_lookup(char *name){
    P(&marble1);
    if (acc_sen == NULL){
        V(&marble1);
        return NULL;
    }
    if ((acc_sen->sen->next == acc_sen->sen) && (acc_sen->sen->prev == acc_sen->sen)){
        struct account *acc_p = malloc(sizeof(struct account));
        struct account new_acc;
        new_acc.balance = 0;
        *acc_p = new_acc;
        acc_p->balance = 0;
        acc_p->inventory = 0;
        char *acc_n = malloc(strlen(name));
        strncpy(acc_n,name,strlen(name));
        acc_p->login_name = acc_n;
        debug("2----Account name is %s",acc_p->login_name);
        debug("2----Account name is %p",acc_p);
        acc_p->sen = acc_sen->sen;
        acc_p->sen->number_of_accounts++;
        acc_sen->sen->next = acc_p;
        acc_sen->sen->prev = acc_p;
        acc_p->next = acc_sen->sen;
        acc_p->prev = acc_sen->sen;
        V(&marble1);
        return acc_p;
    }
    struct account *temp = acc_sen->next;
    int res = -1;
    while (temp != acc_sen->sen){
        res = strcmp(temp->login_name,name);
        if (res == 0){
            V(&marble1);
            return temp;
        }
        temp = temp->next;
    }
    if (acc_sen->number_of_accounts == 64){
        V(&marble1);
        return NULL;
    }
    struct account *acc_p = malloc(sizeof(struct account));
    struct account new_acc;
    new_acc.balance = 0;
    *acc_p = new_acc;
    acc_p->balance = 0;
    acc_p->inventory = 0;
    char *acc_n = malloc(strlen(name));
    strncpy(acc_n,name,strlen(name));
    acc_p->login_name = acc_n;
    debug("3----Account name is %s",acc_p->login_name);
    acc_p->sen = acc_sen->sen;
    acc_p->sen->number_of_accounts++;
    struct account *hold = acc_sen->next;  
    acc_p->sen = acc_sen->sen;
    acc_sen->sen->next = acc_p;            
    hold->prev = acc_p;
    acc_p->next = hold;
    acc_p->prev = acc_sen->sen;
    V(&marble1);
    return acc_p;
}

/*
 * Increase the balance for an account.
 *
 * @param account  The account whose balance is to be increased.
 * @param amount  The amount by which the balance is to be increased.
 */
void account_increase_balance(ACCOUNT *account, funds_t amount){
    P(&marble1);
    account->balance = account->balance + amount;
    debug("1----Account name is %s",account->login_name);
    debug("2----Account pointer is %p",account);
    debug("Increase balance of account %s (%d to %d)",account->login_name,(account->balance - amount),account->balance);
    V(&marble1);
}

/*
 * Attempt to decrease the balance for an account.
 *
 * @param account  The account whose balance is to be decreased.
 * @param amount  The amount by which the balance is to be decreased.
 * @return 0 if the original balance is at least as great as the
 * amount of decrease, -1 otherwise.  In case -1 is returned, there
 * is no change to the account balance.
 */
int account_decrease_balance(ACCOUNT *account, funds_t amount){
    P(&marble1);
    if (account->balance < amount){
        V(&marble1);
        return -1;
    }
    account->balance = account->balance - amount;
    debug("Decrease balance of account %s (%d to %d)",account->login_name,(account->balance + amount),account->balance);
    V(&marble1);
    return 0;
}

/*
 * Increase the inventory of an account by a specified quantity.
 *
 * @param account  The account whose inventory is to be increased.
 * @param amount  The amount by which the inventory is to be increased.
 */
void account_increase_inventory(ACCOUNT *account, quantity_t quantity){
    P(&marble1);
    account->inventory = account->inventory + quantity;
    debug("Increase Inv of account %s (%d to %d)",account->login_name,(account->inventory - quantity),account->inventory);
    V(&marble1);
}

/*
 * Attempt to decrease the inventory for an account by a specified quantity.
 *
 * @param account  The account whose inventory is to be decreased.
 * @param amount  The amount by which the inventory is to be decreased.
 * @return 0 if the original inventory is at least as great as the
 * amount of decrease, -1 otherwise.  In case -1 is returned, there
 * is no change to the account balance.
 */
int account_decrease_inventory(ACCOUNT *account, quantity_t quantity){
    P(&marble1);
    if (account->inventory < quantity){
        V(&marble1);
        return -1;
    }
    account->inventory = account->inventory - quantity;
    debug("Decrease Inv of account %s (%d to %d)",account->login_name,(account->inventory + quantity),account->inventory);
    V(&marble1);
    return 0;
}        

/*
 * Get the current balance and inventory of a specified account.  The values
 * returned are guaranteed to be a consistent snapshot of the state of the
 * account at a single point in time, although the state may change as soon
 * as this function has returned.
 *
 * @param account  the account whose balance and inventory is to be queried.
 * @param infop  pointer to structure to receive the status information.  
 * As this structure is designed to be sent in a packet, multibyte fields will be
 * stored in network byte order.
 */
void account_get_status(ACCOUNT *account, BRS_STATUS_INFO *infop){
    P(&marble1);
    funds_t acc_bal = account->balance;
    quantity_t acc_inv = account->inventory;
    infop->balance = acc_bal;
    infop->inventory = acc_inv;
    V(&marble1);
}