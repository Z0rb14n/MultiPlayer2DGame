package net;

public interface ServerNetworkEventReceiver {
    /**
     * Runs when a client connects to the server
     * @param s
     * @param c
     */
    void clientConnectionEvent(BasicServer s, BasicClient c);
}
