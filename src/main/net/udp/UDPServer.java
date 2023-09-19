package net.udp;

import game.GameLogger;
import net.ByteSerializable;
import net.MagicConstDeserializer;
import util.Pair;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class UDPServer implements Runnable {
    private final ArrayList<UDPServerNetworkEventReceiver> networkEventReceivers = new ArrayList<>(1);
    private volatile Thread thread;
    private final DatagramSocket server;
    private final Object clientsLock = new Object[0];
    private final HashMap<Pair<InetAddress, Integer>, Long> clients = new HashMap<>();
    private final int receiveTimeout;
    private final int inactivityDelay;
    public ArrayList<Pair<byte[], Pair<InetAddress, Integer>>> packets = new ArrayList<>();

    public UDPServer(int port) throws IOException {
        this(port, 1000, 30000);
    }

    /**
     * @param port port used to transfer data
     */
    public UDPServer(int port, int receiveTimeout, int inactivityDelay) throws IOException {
        server = new DatagramSocket(port);
        server.setSoTimeout(receiveTimeout);
        thread = new Thread(this);
        thread.start();
        this.receiveTimeout = receiveTimeout;
        this.inactivityDelay = inactivityDelay;
    }

    public void addNetworkEventReceiver(UDPServerNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.add(networkEventReceiver);
    }

    public void removeNetworkEventReceiver(UDPServerNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.remove(networkEventReceiver);
    }

    public void clearNetworkEventReceivers() {
        networkEventReceivers.clear();
    }

    /**
     * Returns true if this server is still active and hasn't run into any trouble.
     */
    public boolean active() {
        return thread != null;
    }

    static public String ip() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Disconnect all clients and stop the server.
     */
    public void stop() {
        thread = null;
        server.close();
    }


    @Override
    public void run() {
        byte[] buffer = new byte[1 << 16];
        long prev;
        while (Thread.currentThread() == thread) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                prev = System.currentTimeMillis();
                long current = new Date().getTime();
                server.receive(packet);
                GameLogger.getDefault().log("Time since prev: " + (System.currentTimeMillis() - prev) + " ms", GameLogger.Category.NETWORK);
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                packets.add(new Pair<>(data, new Pair<>(packet.getAddress(), packet.getPort())));
                for (UDPServerNetworkEventReceiver networkEventReceiver : networkEventReceivers)
                    networkEventReceiver.onReceiveData(this, data, packet.getAddress(), packet.getPort());
                if (clients.containsKey(new Pair<>(packet.getAddress(), packet.getPort()))) {
                    clients.put(new Pair<>(packet.getAddress(), packet.getPort()), current);
                } else {
                    synchronized (clientsLock) {
                        clients.put(new Pair<>(packet.getAddress(), packet.getPort()), current);
                    }
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
                thread = null;
            }
        }
    }

    public void writePacket(ByteSerializable packet) {
        byte[] data = MagicConstDeserializer.serialize(packet);
        synchronized (clientsLock) {
            Iterator<Map.Entry<Pair<InetAddress, Integer>, Long>> iterator = clients.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Pair<InetAddress, Integer>, Long> entry = iterator.next();
                Pair<InetAddress, Integer> client = entry.getKey();
                long lastActivity = entry.getValue();
                if (new Date().getTime() - lastActivity > inactivityDelay) {
                    for (UDPServerNetworkEventReceiver receiver : networkEventReceivers) {
                        receiver.onInactive(this, client.first, client.second, entry.getValue());
                    }
                    iterator.remove();
                } else {
                    DatagramPacket udpPacket = new DatagramPacket(data, data.length, client.first, client.second);
                    try {
                        server.send(udpPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        synchronized (clientsLock) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    public void writePacket(ByteSerializable packet, InetAddress address, int port) {
        byte[] data = MagicConstDeserializer.serialize(packet);
        DatagramPacket udpPacket = new DatagramPacket(data, data.length, address, port);
        try {
            server.send(udpPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
