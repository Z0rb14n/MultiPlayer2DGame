package game.net;

import game.GameController;
import net.BasicClient;
import net.BasicServer;
import net.ServerNetworkEventReceiver;
import physics.Vec2D;

import java.io.IOException;
import java.util.HashMap;

public class GameServer implements ServerNetworkEventReceiver {
    private final HashMap<Integer, BasicClient> clients = new HashMap<>();
    private final HashMap<BasicClient, Integer> clientIDs = new HashMap<>();
    private final BasicServer server;
    private final GameController controller = GameController.getInstance();
    private int nextID = 0;
    public GameServer() throws IOException {
        server = new BasicServer(NetworkConstants.PORT);
        server.addNetworkEventReceiver(this);
        InputPacket.ensureFactoryRegistered();
    }

    @Override
    public void clientConnectionEvent(BasicServer s, BasicClient c) {
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
        controller.addVehicle(new Vec2D(100,100), id);
    }

    @Override
    public void removeClientEvent(BasicServer s, BasicClient c) {
        assert(s == server);
        int id = clientIDs.get(c);
        controller.removeVehicle(id);
        clients.remove(id);
        clientIDs.remove(c);
    }

    public void update() {
        controller.update();
        controller.processInputs(getInputs());
        controller.processMovementInputs();
        server.writePacket(controller.asGameStatePacket());
    }

    private HashMap<Integer, InputPacket> getInputs() {
        HashMap<Integer, InputPacket> inputs = new HashMap<>();
        for (BasicClient c : clients.values()) {
            // note we may have multiple packets from the same client
            InputPacket packet = (InputPacket) c.readPacket();
            if (packet == null) continue;
            System.out.println("Received input: " + packet);
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
