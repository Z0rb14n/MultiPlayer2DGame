package net;

/**
 * Receiver for Server/Client events
 */
public interface ClientNetworkEventReceiver {

    /**
     * Runs when a client has received data from the server (called by ModifiedClient)
     * @param c
     */
    void dataReceivedEvent(BasicClient c);

    /**
     * Runs when a client disconnects from the server (called by ModifiedClient)
     * @param c
     */
    void disconnectEvent(BasicClient c);

    /**
     * Runs when a client encounters end-of-stream (e.g. host disconnect, kick)
     * @param c
     */
    void endOfStreamEvent(BasicClient c);
}
