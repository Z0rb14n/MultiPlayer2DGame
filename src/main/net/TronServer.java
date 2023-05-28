package net;

import java.io.IOException;

public class TronServer implements NetworkEventReceiver {
    private ModifiedClient[] clients = new ModifiedClient[2]; // player 1, player 2
    private ModifiedServer server;
    public TronServer() {
        try {
            server = new ModifiedServer(NetworkConstants.PORT);
            server.addNetworkEventReceiver(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clientConnectionEvent(ModifiedServer s, ModifiedClient c) {
        assert(s == server);

    }

    @Override
    public void disconnectEvent(ModifiedClient c) {
        if (c == clients[0]) clients[0] = null;
        else if (c == clients[1]) clients[1] = null;
        else System.out.println("Disconnected client not in clients list.");
    }
}
