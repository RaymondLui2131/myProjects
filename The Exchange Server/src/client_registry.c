#include <client_registry.h>
#include <errno.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <debug.h>
#include <string.h>
#include <stdlib.h>
#include <csapp.h>

sem_t marble2;
void Sem_init(sem_t *sem, int pshared, unsigned int value);
void P(sem_t *sem);
void V(sem_t *sem);

CLIENT_REGISTRY *creg_init();
void creg_fini(CLIENT_REGISTRY *cr);
int creg_register(CLIENT_REGISTRY *cr, int fd);
int creg_unregister(CLIENT_REGISTRY *cr, int fd);
void creg_wait_for_empty(CLIENT_REGISTRY *cr);
void creg_shutdown_all(CLIENT_REGISTRY *cr);

struct client_registry{
    int fd;
    int size;
    struct client_registry *sen;
    struct client_registry *next;
    struct client_registry *prev;
}the_client;

int client_init_bool = 0;
/*
 * Initialize a new client registry.
 *
 * @return  the newly initialized client registry, or NULL if initialization
 * fails.
 */
CLIENT_REGISTRY *creg_init(){
    if (client_init_bool == 1) return NULL;
    Sem_init(&marble2,0,1);
    struct client_registry *cr_p = malloc(sizeof(struct client_registry));
    if (cr_p == NULL) return NULL;
    struct client_registry new_client;
    new_client.size = 0;
    *cr_p = new_client;
    cr_p->size = 0;
    cr_p->fd = -1;
    cr_p->sen = cr_p;
    cr_p->next = cr_p;
    cr_p->prev = cr_p;
    client_init_bool = 1;
    return cr_p;
}

/*
 * Finalize a client registry, freeing all associated resources.
 *
 * @param cr  The client registry to be finalized, which must not
 * be referenced again.
 */
void creg_fini(CLIENT_REGISTRY *cr){
    if (client_init_bool == 0) return;
    P(&marble2);
    if ((cr->sen->next == cr->sen) && (cr->sen->prev == cr->sen)){
        debug("Freeing Sen %p",cr->sen);
        free(cr->sen);
        V(&marble2);
        return;
    } 
    struct client_registry *temp = cr->sen->next;
    struct client_registry *hold = temp;
    while (temp != cr->sen){
        hold = temp->next;
        free(temp);
        temp = hold;
    }
    debug("Freeing Sen");
    free(cr->sen);
    V(&marble2);
}

/*
 * Register a client file descriptor.
 *
 * @param cr  The client registry.
 * @param fd  The file descriptor to be registered.
 * @return 0 if registration succeeds, otherwise -1.
 */
int creg_register(CLIENT_REGISTRY *cr, int fd){     
    //debug("Hello cr");
    P(&marble2);
    cr->sen->size++;
    struct client_registry *cr_p = malloc(sizeof(struct client_registry));
    if (cr_p == NULL) return -1;
    struct client_registry new_client;
    new_client.fd = fd;
    *cr_p = new_client;
    cr_p->fd = fd;
    struct client_registry *hold = cr->sen->next;    
    cr_p->sen = cr->sen;
    cr->sen->next = cr_p;                      // cr.sen -> new_client -> cr.sen.next -> cr.sen
    hold->prev = cr_p;
    cr_p->next = hold;
    cr_p->prev = cr->sen;
    // debug("Sen next is : %p",cr->sen->next);
    // debug("Sen prev is : %p",cr->sen->prev);
    // debug("Client next is : %p",cr_p->next);
    // debug("Client prev is : %p",cr_p->prev);
    debug("NUMBER OF CLIENTS IS :%d",cr->sen->size);
    V(&marble2);
    return 0;
}

/*
 * Unregister a client file descriptor, alerting anybody waiting
 * for the registered set to become empty.
 *
 * @param cr  The client registry.
 * @param fd  The file descriptor to be unregistered.
 * @return 0  if unregistration succeeds, otherwise -1.
 */
int creg_unregister(CLIENT_REGISTRY *cr, int fd){
    P(&marble2);
    if ((cr->sen->next == cr->sen) && (cr->sen->prev == cr->sen)){
        V(&marble2);
        return -1;
    }
    cr->sen->size--;
    struct client_registry *temp = cr->sen->next;
    struct client_registry *hold = temp;
    while (temp != cr->sen){
        hold = temp->next;
        if (temp->fd == fd){
            struct client_registry *t_next = temp->next;
            struct client_registry *t_prev = temp->prev;
            t_prev->next = t_next;
            t_next->prev = t_prev;
            debug("Shutting down fd %d",temp->fd);
            shutdown(temp->fd,SHUT_RD);
            free(temp);
            V(&marble2);
            return 0;
        }
        temp = hold;
    }
    V(&marble2);
    return -1;
}

/*
 * A thread calling this function will block in the call until
 * the number of registered clients has reached zero, at which
 * point the function will return.
 *
 * @param cr  The client registry.
 */
void creg_wait_for_empty(CLIENT_REGISTRY *cr){
    //debug("Hello ce");
    while (1){
        //debug("Uno");
        P(&marble2);
        //debug("Uno2");
        int size = cr->sen->size;
        //debug("Size is %d",size);
        if (size == 0){
            V(&marble2);
            return;
        } 
        V(&marble2);
        //debug("Past v ce");
    }
}

/*
 * Shut down all the currently registered client file descriptors.
 *
 * @param cr  The client registry.
 */
void creg_shutdown_all(CLIENT_REGISTRY *cr){
    P(&marble2);
    if ((cr->sen->next == cr->sen) && (cr->sen->prev == cr->sen)){
        V(&marble2);
        return;
    }
    struct client_registry *temp = cr->sen->next;
    while (temp != cr->sen){
        int file_d = temp->fd;
        shutdown(file_d,SHUT_RD);
        temp = temp->next;
    }
    V(&marble2);
}
