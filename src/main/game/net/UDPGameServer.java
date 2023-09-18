package game.net;

import game.GameController;
import game.GameLogger;
import net.ByteSerializable;
import net.MagicConstDeserializer;
import net.udp.UDPServer;
import net.udp.UDPServerNetworkEventReceiver;
import physics.Vec2D;
import util.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class UDPGameServer implements UDPServerNetworkEventReceiver {
    private final HashMap<Integer, Pair<InetAddress, Integer>> clients = new HashMap<>();
    private final HashMap<Pair<InetAddress, Integer>, Integer> clientIDs = new HashMap<>();
    private final HashMap<Integer, ArrayList<InputPacket>> inputs = new HashMap<>();
    private final UDPServer server;
    private final GameController controller = GameController.getInstance();
    private final Object lock = new Object[0];
    private int nextID = 0;
    public UDPGameServer() throws IOException {
        server = new UDPServer(NetworkConstants.PORT);
        server.addNetworkEventReceiver(this);
        InputPacket.ensureFactoryRegistered();
        RespawnRequestPacket.ensureFactoryRegistered();
    }

    @Override
    public void onReceiveData(UDPServer s, byte[] packetData, InetAddress clientAddress, int clientPort) {
        assert (s == server);
        if (clientIDs.containsKey(new Pair<>(clientAddress, clientPort))) {
            int id = clientIDs.get(new Pair<>(clientAddress, clientPort));
            ByteSerializable packet = MagicConstDeserializer.deserialize(packetData, 0, packetData.length);
            if (packet instanceof InputPacket) {
                synchronized (lock) {
                    if (!inputs.containsKey(id)) {
                        inputs.put(id, new ArrayList<>());
                    }
                    inputs.get(id).add((InputPacket) packet);
                }
            } else if (packet instanceof RespawnRequestPacket) {
                controller.respawn(id);
            }
        } else {
            int id = nextID++;
            Integer[] ids = clients.keySet().toArray(new Integer[0]);
            int[] ids2 = new int[ids.length];
            for (int i = 0; i < ids.length; i++) {
                ids2[i] = ids[i];
            }
            clients.put(id, new Pair<>(clientAddress, clientPort));
            clientIDs.put(new Pair<>(clientAddress, clientPort), id);
            InitGameInfoPacket packet = new InitGameInfoPacket(id, ids2);
            server.writePacket(packet, clientAddress, clientPort);
            System.out.println("added id: " + id);
            controller.addVehicle(new Vec2D(100,100), id);
        }
    }

    @Override
    public void onInactive(UDPServer server, InetAddress clientAddress, int clientPort, long lastReceived) {
        Pair<InetAddress, Integer> clientData = new Pair<>(clientAddress, clientPort);
        Integer id = clientIDs.get(clientData);
        if (id == null) return;
        controller.removeVehicle(id);
        clientIDs.remove(clientData);
        clients.remove(id);
    }

    private long lastPacket = 0;

    public void update() {
        controller.update();
        controller.processInputs(getInputs());
        controller.processMovementInputs();
        server.writePacket(controller.asGameStatePacket());
        long diff = System.currentTimeMillis() - lastPacket;
        GameLogger.getDefault().log("Packet Diff: " + diff, "PERFORMANCE");
        lastPacket = System.currentTimeMillis();
    }

    private HashMap<Integer, InputPacket> getInputs() {
        HashMap<Integer, InputPacket> finalInputs = new HashMap<>();

        for (Integer client : inputs.keySet()) {
            InputPacket packet = null;
            synchronized (lock) {
                for (InputPacket p : inputs.get(client)) {
                    packet = InputPacket.add(packet, p);
                }
                inputs.get(client).clear();
            }
            if (packet != null) finalInputs.put(client, packet);
        }

        return finalInputs;
    }
}
