#include <protocol.h>
#include <errno.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <debug.h>
#include <string.h>
#include <stdlib.h>
#include <arpa/inet.h>


int proto_send_packet(int fd, BRS_PACKET_HEADER *hdr, void *payload);
int proto_recv_packet(int fd, BRS_PACKET_HEADER *hdr, void **payloadp);

int readHelper(int fd, char *buf, size_t count);
int writeHelper(int fd, char *buf, size_t count);


/*
 * Send a packet, which consists of a fixed-size header followed by an
 * optional associated data payload.
 *
 * @param fd  The file descriptor on which packet is to be sent.
 * @param pkt  The fixed-size packet header, with multi-byte fields
 *   in network byte order
 * @param data  The data payload, or NULL, if there is none.
 * @return  0 in case of successful transmission, -1 otherwise.
 *   In the latter case, errno is set to indicate the error.
 *
 * All multi-byte fields in the packet are assumed to be in network byte order.
 */
int proto_send_packet(int fd, BRS_PACKET_HEADER *hdr, void *payload){
    int res = 0;
    char *data_hdr = (char*)hdr;
    size_t hdr_size = sizeof(struct brs_packet_header);
    res = writeHelper(fd,data_hdr,hdr_size); 

    if ((res == -1) || (res == 0)) return -1;

    if (hdr->size == 0){
        return 0;
    }

    size_t payload_size = ntohs(hdr->size);
    res = writeHelper(fd,payload,payload_size);
    
    if (res == -1) return -1;

    return 0;
}

/*
 * Receive a packet, blocking until one is available.
 *
 * @param fd  The file descriptor from which the packet is to be received.
 * @param pkt  Pointer to caller-supplied storage for the fixed-size
 *   portion of the packet.
 * @param datap  Pointer to a variable into which to store a pointer to any
 *   payload received.
 * @return  0 in case of successful reception, -1 otherwise.  In the
 *   latter case, errno is set to indicate the error.
 *
 * The returned packet has all multi-byte fields in network byte order.
 * If the returned payload pointer is non-NULL, then the caller has the
 * responsibility of freeing that storage.
 */
int proto_recv_packet(int fd, BRS_PACKET_HEADER *hdr, void **payloadp){
    int res = 0;
    char *data_hdr = (char*)hdr;
    size_t hdr_size = sizeof(struct brs_packet_header);
    //debug("1 Hdr_size is equal to %ld",hdr_size);
    res = readHelper(fd,data_hdr,hdr_size); 
    //debug("2 First read is equal to %d",res);
    if ((res == -1) || (res == 0)) return -1;

    if (hdr->size == 0){
        return 0;
    }
    // debug("3 Hdr size is %d",hdr->size);
    size_t final_size = ntohs(hdr->size);
    //debug("3 Hdr size is %d",hdr->size);
    void *m_playload = malloc(final_size);
    res = readHelper(fd,(char*)m_playload,final_size);
    // debug("Second read is equal to %d",res);
    if (res == -1) return -1;
    if (payloadp != NULL) *payloadp = m_playload;
    return 0;
}

int readHelper(int fd, char *buf, size_t count){
    int count_helper = count;
    while (count_helper > 0){
        ssize_t result = read(fd,buf,count_helper);
        //debug("RESULT IS %ld",result);
        if (result == -1){
            debug("readHelper error");
            debug("Errno code is %d",errno);
            return -1;
        }
        else if (result == 0){
            return 0;
        }
        buf += result;
        count_helper -= result;
        //debug("COUNT_HELPER IS %d",count_helper);
    }
    //debug("ReadHelper is returning");
    return 2;
}

int writeHelper(int fd, char *buf, size_t count){
    int count_helper = count;
    while (count_helper > 0){
        ssize_t result = write(fd,buf,count_helper);
        if (result == -1){
            debug("writeHelper error");
            debug("Errno code is %d",errno);
            return -1;
        }
        else if (result == 0){
            return 0;
        }
        buf += result;
        count_helper -= result;
    }
    return 2;
}