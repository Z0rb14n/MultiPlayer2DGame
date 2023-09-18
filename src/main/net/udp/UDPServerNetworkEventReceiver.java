package net.udp;

import java.net.InetAddress;

public interface UDPServerNetworkEventReceiver {
    void onReceiveData(UDPServer server, byte[] packetData, InetAddress clientAddress, int clientPort);

    void onInactive(UDPServer server, InetAddress clientAddress, int clientPort, long lastReceived);
}
