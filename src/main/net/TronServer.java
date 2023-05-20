package net;

public class TronServer implements EventReceiver {
    private ModifiedClient[] clients = new ModifiedClient[2]; // player 1, player 2
    private ModifiedServer server;
    public TronServer() {
        server = new ModifiedServer(this,NetworkConstants.PORT);
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
