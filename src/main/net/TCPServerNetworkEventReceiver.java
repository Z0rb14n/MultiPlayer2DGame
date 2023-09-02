package net;

public interface TCPServerNetworkEventReceiver {
    /**
     * Runs when a client connects to the server
     * @param s
     * @param c
     */
    void clientConnectionEvent(TCPServer s, TCPClient c);

    void removeClientEvent(TCPServer s, TCPClient c);
}
