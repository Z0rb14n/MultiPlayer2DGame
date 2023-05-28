package net;

/**
 * Receiver for Server/Client events
 */
public interface NetworkEventReceiver {
    /**
     * Runs when a client connects to the server (called by ModifiedServer)
     * @param s
     * @param c
     */
    default void clientConnectionEvent(BasicServer s, BasicClient c) {
    }

    /**
     * Runs when a client has received data from the server (called by ModifiedClient)
     * @param c
     */
    default void dataReceivedEvent(BasicClient c) {
    }

    /**
     * Runs when a client disconnects from the server (called by ModifiedClient)
     * @param c
     */
    default void disconnectEvent(BasicClient c) {
    }

    /**
     * Runs when a client encounters end-of-stream (e.g. host disconnect, kick)
     * @param c
     */
    default void endOfStreamEvent(BasicClient c) {
    }
}
