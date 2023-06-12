package game.net;

import game.GameController;
import net.BasicClient;
import net.BasicServer;
import net.NetworkEventReceiver;

import java.io.IOException;
import java.util.HashMap;

public class GameServer implements NetworkEventReceiver {
    private final HashMap<Integer, BasicClient> clients = new HashMap<>();
    private final HashMap<BasicClient, Integer> clientIDs = new HashMap<>();
    private final BasicServer server;
    private final GameController controller = GameController.getInstance();
    private int nextID = 0;
    public GameServer() throws IOException {
        server = new BasicServer(NetworkConstants.PORT);
        server.addNetworkEventReceiver(this);
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
    }

    public void update() {
        controller.update();
    }

    @Override
    public void dataReceivedEvent(BasicClient c) {
    }

    @Override
    public void disconnectEvent(BasicClient c) {
        int id = clientIDs.get(c);
        clients.remove(id);
        clientIDs.remove(c);
    }

    @Override
    public void endOfStreamEvent(BasicClient c) {
    }
}
