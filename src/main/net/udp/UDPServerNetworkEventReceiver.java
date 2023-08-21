package net.udp;

import java.net.InetAddress;

public interface UDPServerNetworkEventReceiver {
    void onReceiveData(UDPServer server, byte[] packetData, InetAddress clientAddress, int clientPort);
}
