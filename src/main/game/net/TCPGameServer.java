package game.net;

import game.GameController;
import game.GameLogger;
import net.TCPClient;
import net.TCPServer;
import net.TCPServerNetworkEventReceiver;

import java.io.IOException;
import java.util.HashMap;

public class TCPGameServer implements TCPServerNetworkEventReceiver {
    private final HashMap<Integer, TCPClient> clients = new HashMap<>();
    private final HashMap<TCPClient, Integer> clientIDs = new HashMap<>();
    private final TCPServer server;
    private final GameController controller = GameController.getInstance();
    private int nextID = 0;
    public TCPGameServer() throws IOException {
        server = new TCPServer(NetworkConstants.PORT);
        server.addNetworkEventReceiver(this);
        InputPacket.ensureFactoryRegistered();
    }

    @Override
    public void clientConnectionEvent(TCPServer s, TCPClient c) {
        assert(s == server);
        int id = nextID++;
        Integer[] ids = clients.keySet().toArray(new Integer[0]);
        int[] ids2 = new int[ids.length];
        for (int i = 0; i < ids.length; i++) {
            ids2[i] = ids[i];
        }
        clients.put(id,c);
        clientIDs.put(c,id);
        InitGameInfoPacket packet = new InitGameInfoPacket(id, ids2);
        c.writePacket(packet);
        controller.addVehicle(id);
    }

    @Override
    public void removeClientEvent(TCPServer s, TCPClient c) {
        assert(s == server);
        int id = clientIDs.get(c);
        controller.removeVehicle(id);
        clients.remove(id);
        clientIDs.remove(c);
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
        HashMap<Integer, InputPacket> inputs = new HashMap<>();
        for (TCPClient c : clients.values()) {
            // note we may have multiple packets from the same client
            InputPacket packet = (InputPacket) c.readPacket();
            if (packet == null) continue;
            while (c.available() > 0) {
                InputPacket read = (InputPacket) c.readPacket();
                packet = InputPacket.add(packet, read);
                if (read == null) break;
            }
            if (packet != null) {
                inputs.put(clientIDs.get(c), packet);
            }
        }
        return inputs;
    }
}
