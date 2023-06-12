package game.net;

import game.GameController;
import net.BasicClient;
import net.BasicServer;
import net.NetworkEventReceiver;

import java.io.IOException;
import java.util.ArrayList;

public class GameServer implements NetworkEventReceiver {
    private final ArrayList<BasicClient> clients = new ArrayList<>();
    private final BasicServer server;
    private final GameController controller = GameController.getInstance();
    public GameServer() throws IOException {
        server = new BasicServer(NetworkConstants.PORT);
        server.addNetworkEventReceiver(this);
    }

    @Override
    public void clientConnectionEvent(BasicServer s, BasicClient c) {
        assert(s == server);
        clients.add(c);

    }

    public void update() {
        controller.update();
    }

    @Override
    public void dataReceivedEvent(BasicClient c) {
    }

    @Override
    public void disconnectEvent(BasicClient c) {
        clients.remove(c);
    }

    @Override
    public void endOfStreamEvent(BasicClient c) {
    }
}
