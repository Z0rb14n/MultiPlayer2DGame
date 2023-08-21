package net.udp;

public interface UDPClientNetworkEventReceiver {
    /**
     * Runs when a client has received data from the server (called by UDPClient).
     * @param c client that received data
     * @param data data received
     */
    void dataReceivedEvent(UDPClient c, byte[] data);
}
