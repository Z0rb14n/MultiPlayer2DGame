package game.net;

import net.BasicClient;
import net.BasicServer;
import net.NetworkEventReceiver;

import java.io.IOException;

public class GameServer implements NetworkEventReceiver {
    private BasicClient[] clients = new BasicClient[2]; // player 1, player 2
    private BasicServer server;
    public GameServer() {
        try {
            server = new BasicServer(NetworkConstants.PORT);
            server.addNetworkEventReceiver(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clientConnectionEvent(BasicServer s, BasicClient c) {
        assert(s == server);

    }

    @Override
    public void dataReceivedEvent(BasicClient c) {
    }

    @Override
    public void disconnectEvent(BasicClient c) {
        if (c == clients[0]) clients[0] = null;
        else if (c == clients[1]) clients[1] = null;
        else System.out.println("Disconnected client not in clients list.");
    }

    @Override
    public void endOfStreamEvent(BasicClient c) {
    }
}
