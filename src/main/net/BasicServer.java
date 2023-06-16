package net;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Processing's Server class modified to not use PApplet
 */
public class BasicServer implements Runnable {
    private final ArrayList<ServerNetworkEventReceiver> networkEventReceivers = new ArrayList<>(1);
    private volatile Thread thread;
    private ServerSocket server;
    private final Object clientsLock = new Object[0];
    private ArrayList<BasicClient> clients = new ArrayList<>(10);

    /**
     * @param port port used to transfer data
     */
    public BasicServer(int port) throws IOException {
        server = new ServerSocket(port);
        thread = new Thread(this);
        thread.start();
    }

    public void addNetworkEventReceiver(ServerNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.add(networkEventReceiver);
    }

    public void removeNetworkEventReceiver(ServerNetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.remove(networkEventReceiver);
    }

    public void clearNetworkEventReceivers() {
        networkEventReceivers.clear();
    }


    /**
     * Disconnect a particular client
     * @param client the client to disconnect
     */
    public void disconnect(BasicClient client) {
        client.stop();
        synchronized (clientsLock) {
            int index = clientIndex(client);
            if (index != -1) {
                removeIndex(index);
            }
        }
    }

    private void removeIndex(int index) {
        synchronized (clientsLock) {
            for (ServerNetworkEventReceiver networkEventReceiver : networkEventReceivers)
                networkEventReceiver.removeClientEvent(this, clients.get(index));
            clients.remove(index);
        }
    }

    private void disconnectAll() {
        synchronized (clientsLock) {
            while (!clients.isEmpty()) {
                try {
                    clients.get(0).stop();
                } catch (Exception ignored) {
                }
                for (ServerNetworkEventReceiver networkEventReceiver : networkEventReceivers)
                    networkEventReceiver.removeClientEvent(this, clients.get(0));
                clients.remove(0);
            }
        }
    }


    private void addClient(BasicClient client) {
        synchronized (clientsLock) {
            clients.add(client);
        }
    }


    private int clientIndex(BasicClient client) {
        synchronized (clientsLock) {
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i) == client) return i;
            }
            return -1;
        }
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


    // the last index used for available. can't just cycle through
    // the clients in order from 0 each time, because if client 0 won't
    // shut up, then the rest of the clients will never be heard from.
    private int lastAvailable = -1;

    /**
     * Returns the next client in line with a new message.
     * <p>
     * Client may be dead - check for active.
     */
    public BasicClient available() {
        synchronized (clientsLock) {
            int index = lastAvailable + 1;
            if (index >= clients.size()) index = 0;

            for (int i = 0; i < clients.size(); i++) {
                int which = (index + i) % clients.size();
                BasicClient client = clients.get(which);
                //Check for valid client
                if (!client.active()) {
                    removeIndex(which);  //Remove dead client
                    i--;                 //Don't skip the next client
                    //If the client has data make sure lastAvailable
                    //doesn't end up skipping the next client
                    which--;
                    //fall through to allow data from dead clients
                    //to be retrieved.
                }
                if (client.available() > 0) {
                    lastAvailable = which;
                    return client;
                }
            }
        }
        return null;
    }

    /**
     * Disconnect all clients and stop the server.
     */
    public void dispose() {
        thread = null;

        if (clients != null) {
            disconnectAll();
            clients = null;
        }
        try {
            if (server != null) {
                server.close();
                server = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (Thread.currentThread() == thread) {
            try {
                Socket socket = server.accept();
                BasicClient client = new BasicClient(socket);
                synchronized (clientsLock) {
                    addClient(client);
                    for(ServerNetworkEventReceiver networkEventReceiver : networkEventReceivers)
                        networkEventReceiver.clientConnectionEvent(this, client);
                }
            } catch (SocketException e) {
                //thrown when server.close() is called and server is waiting on accept
                System.err.println("Server SocketException: " + e.getMessage());
                thread = null;
            } catch (IOException e) {
                //errorMessage("run", e);
                e.printStackTrace();
                thread = null;
            }
        }
    }


    /**
     * Writes a value to all the connected clients. It sends bytes out from the
     * Server object.
     *
     * @param data data to write
     */
    public void writeByte(byte data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeByte(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeBytes(byte[] data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeBytes(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeShort(short data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeShort(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeInt(int data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeInt(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeLong(long data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeLong(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeFloat(float data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeFloat(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writeStr(String data) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writeStr(data);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }

    public void writePacket(ByteSerializable packet) {
        synchronized (clientsLock) {
            int index = 0;
            while (index < clients.size()) {
                if (clients.get(index).active()) {
                    clients.get(index).writePacket(packet);
                    index++;
                } else {
                    removeIndex(index);
                }
            }
        }
    }
}
