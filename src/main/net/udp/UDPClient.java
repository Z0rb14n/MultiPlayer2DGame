package net.udp;

import game.GameLogger;
import net.ByteSerializable;
import net.TCPClientNetworkEventReceiver;
import net.MagicConstDeserializer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class UDPClient implements Runnable {
    private final ArrayList<UDPClientNetworkEventReceiver> networkEventReceivers = new ArrayList<>(1);
    private volatile Thread thread;
    private DatagramSocket socket;
    public ArrayDeque<byte[]> packets = new ArrayDeque<>();
    private final InetAddress hostName;
    private final int hostPort;

    public UDPClient(String host, int hostPort) throws IOException {
        this(host, hostPort, 1000);
    }

    public UDPClient(String host, int hostPort, int timeout) throws IOException {
        this.socket = new DatagramSocket();
        hostName = InetAddress.getByName(host);
        this.hostPort = hostPort;
        socket.setSoTimeout(timeout);
        thread = new Thread(this, "ClientThread");
        thread.start();
        GameLogger.getDefault().log("Created UDP socket: " + this.socket.getPort() + "," + this.socket.getLocalPort(), GameLogger.Category.NETWORK);
    }

    public UDPClient(String host, int hostPort, int clientPort, int timeout) throws IOException {
        this.socket = new DatagramSocket(clientPort);
        hostName = InetAddress.getByName(host);
        this.hostPort = hostPort;
        socket.setSoTimeout(timeout);
        thread = new Thread(this, "ClientThread");
        thread.start();
    }

    public void addNetworkEventReceiver(UDPClientNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.add(networkEventReceiver);
    }

    public void removeNetworkEventReceiver(UDPClientNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.remove(networkEventReceiver);
    }

    public void clearNetworkEventReceivers() {
        networkEventReceivers.clear();
    }

    /**
     * Disconnect from the server and frees resources.
     */
    public void stop() {
        thread = null;
        networkEventReceivers.clear();

        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] readBuffer;
        { // make the read buffer same size as socket receive buffer so that
            // we don't waste cycles calling listeners when there is more data waiting
            int readBufferSize = 1 << 16; // 64 KB (default socket receive buffer size)
            try {
                readBufferSize = socket.getReceiveBufferSize();
            } catch (SocketException ignore) {
            }
            readBuffer = new byte[readBufferSize];
        }
        while (Thread.currentThread() == thread) {
            while (!socket.isClosed()) {
                DatagramPacket packet = new DatagramPacket(readBuffer, readBuffer.length);
                try {
                    socket.receive(packet);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), 0, data, 0, data.length);
                    packets.add(data);

                    for (UDPClientNetworkEventReceiver networkEventReceiver : networkEventReceivers)
                        networkEventReceiver.dataReceivedEvent(this, data);
                } catch (SocketTimeoutException ignored) {
                    GameLogger.getDefault().log("UDPClient receive Socket timeout", GameLogger.Level.WARNING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * Returns true if this client is still active and hasn't run
     * into any trouble.
     */
    public boolean active() {
        return thread != null && socket != null && !socket.isClosed();
    }


    /**
     * Returns the IP address of the computer to which the Client is attached.
     */
    public String ip() {
        if (socket != null) {
            return socket.getInetAddress().getHostAddress();
        }
        return null;
    }


    /**
     * Empty the buffer, removes all the data stored there.
     */
    public void clear() {
        packets.clear();
    }

    public void writePacket(ByteSerializable packet) {
        byte[] bytes = MagicConstDeserializer.serialize(packet);
        DatagramPacket udpPacket = new DatagramPacket(bytes, bytes.length, hostName, hostPort);

        try {
            socket.send(udpPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ByteSerializable readPacket() {
        if (packets.isEmpty()) return null;
        byte[] packet = packets.remove();
        return MagicConstDeserializer.deserialize(packet, 0, packet.length);
    }
}
