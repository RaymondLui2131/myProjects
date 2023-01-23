#define _XOPEN_SOURCE 700
#include <stdlib.h>

#include "client_registry.h"
#include "exchange.h"
#include "account.h"
#include "trader.h"
#include "debug.h"
#include "server.h"
#include "csapp.h"

#include <signal.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/wait.h>
#include <sys/socket.h>

extern EXCHANGE *exchange;
extern CLIENT_REGISTRY *client_registry;

static void terminate(int status);
void SIG_HUP_handler(int sig);

volatile sig_atomic_t SIG_HUP = 0;

int *m_pointer = NULL;

/*
 * "Bourse" exchange server.
 *
 * Usage: bourse <port>
 */
int main(int argc, char* argv[]){
    // Option processing should be performed here.
    // Option '-p <port>' is required in order to specify the port number
    // on which the server should listen.
    int flag_bool = 0;
    int option;
    //char *portNumber = NULL;
    int portNumber;
    while ((option = getopt(argc,argv,":p:h:q")) != -1){
        switch (option)
        {
        case 'p':
            portNumber = atoi(optarg);
            if ((portNumber < 1) || (portNumber > 65535)) return EXIT_FAILURE; // 1 through 65535
            debug("The p num is %d",portNumber);
            debug("The pid is %d",getpid());
            flag_bool = 1;
            break;
        case 'h':
            break;
        case 'q':
            break;
        default:
            debug("Please enter valid port number"); 
        }
    }
    if (flag_bool == 0) return EXIT_FAILURE;
    // Perform required initializations of the client_registry,
    // maze, and player modules.
    client_registry = creg_init();
    accounts_init(); 
    traders_init();
    exchange = exchange_init();

    // TODO: Set up the server socket and enter a loop to accept connections
    // on this socket.  For each connection, a thread should be started to
    // run function brs_client_service().  In addition, you should install
    // a SIGHUP handler, so that receipt of SIGHUP will perform a clean
    // shutdown of the server.
    
    struct sigaction actionHUP = {0};
    actionHUP.sa_handler = &SIG_HUP_handler;
    actionHUP.sa_flags = SA_RESTART;
    sigaction(SIGHUP,&actionHUP,NULL);

    int listenfd, *connfdp;
    socklen_t clientlen;
    struct sockaddr_storage clientaddr;
    pthread_t tid;

    // int portInt = atoi(portNumber);
    //debug("Port number is %d",portNumber);
    listenfd = Open_listenfd(portNumber);
    //debug("Listenfd is %d",listenfd);
    if (listenfd == -1){
        debug("Invalid port number");
        terminate(EXIT_FAILURE);
    }
    
    while (1) {
        clientlen = sizeof(struct sockaddr_storage);
        connfdp = malloc(sizeof(int));
        m_pointer = connfdp;
        *connfdp = Accept(listenfd,(SA *) &clientaddr, &clientlen);
        pthread_create(&tid, NULL, brs_client_service, connfdp);
    }

    // fprintf(stderr, "You have to finish implementing main() "
	//     "before the Bourse server will function.\n");

    terminate(EXIT_SUCCESS);
}

/*
 * Function called to cleanly shut down the server.
 */
static void terminate(int status) {
    // Shutdown all client connections.
    // This will trigger the eventual termination of service threads.
    creg_shutdown_all(client_registry);
    
    debug("Waiting for service threads to terminate...");
    creg_wait_for_empty(client_registry);
    debug("All service threads terminated.");

    // Finalize modules.
    creg_fini(client_registry);
    exchange_fini(exchange);
    traders_fini();
    accounts_fini();

    debug("Bourse server terminating");
    exit(status);
}

void SIG_HUP_handler(int sig){
    sigset_t mask_all, prev_all;
    int olderrno = errno;
    sigfillset(&mask_all);
    sigprocmask(SIG_BLOCK,&mask_all,&prev_all);
    SIG_HUP = 1;
    if (m_pointer != NULL) free(m_pointer);
    m_pointer = NULL;
    terminate(EXIT_SUCCESS);
    sigprocmask(SIG_SETMASK,&prev_all,NULL);
    errno = olderrno;
}

// HOW TO CHECK IF if initialization fails
