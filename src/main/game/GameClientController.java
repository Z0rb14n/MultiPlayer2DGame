package game;

import game.net.InitGameInfoPacket;
import game.net.NetworkConstants;
import net.BasicClient;
import net.BasicServer;
import net.ByteSerializable;
import net.NetworkEventReceiver;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class GameClientController implements NetworkEventReceiver {
    private static GameClientController singleton;

    private GameClientController() {
    }

    public static GameClientController getInstance() {
        if (singleton == null) singleton = new GameClientController();
        return singleton;
    }

    private BasicClient client;
    private int playerNumber;
    private final ArrayList<Integer> currentPlayers = new ArrayList<>();
    public NetworkInstantiationResult connect(String ip) {
        try {
            GameLogger.getDefault().log("Starting instantiation of client...", "NETWORK");
            client = new BasicClient(ip, NetworkConstants.PORT, 5000);
            client.addNetworkEventReceiver(this);
            GameLogger.getDefault().log("Client instantiated.", "NETWORK");
            Thread.sleep(100);
            InitGameInfoPacket.ensureFactoryInitialized();
            ByteSerializable packet = client.readPacket();
            if (packet == null) {
                GameLogger.getDefault().log("Packet was null.", "NETWORK");
                client.stop();
                client = null;
                return NetworkInstantiationResult.FAILURE;
            }
            if (!(packet instanceof InitGameInfoPacket)) {
                // what?
                GameLogger.getDefault().log("Packet was not InitGameInfoPacket.", "NETWORK");
                client.stop();
                client = null;
                return NetworkInstantiationResult.FAILURE;
            }
            InitGameInfoPacket initPacket = (InitGameInfoPacket) packet;
            GameLogger.getDefault().log("Received InitGameInfoPacket.", "NETWORK");
            playerNumber = initPacket.getYourPlayerID();
            for (int i = 0; i < initPacket.getActivePlayers().length; i++) {
                currentPlayers.add(i);
            }
            return NetworkInstantiationResult.SUCCESS;
        } catch (SocketException ex) {
            if (ex.getMessage().contains("already connected")) {
                GameLogger.getDefault().log("Already connected to server.", "NETWORK");
                return NetworkInstantiationResult.ALREADY_CONNECTED;
            } else if (ex.getMessage().contains("timed out")) {
                return NetworkInstantiationResult.TIMEOUT;
            } else {
                ex.printStackTrace();
                return NetworkInstantiationResult.FAILURE;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return NetworkInstantiationResult.FAILURE;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public void clientConnectionEvent(BasicServer s, BasicClient c) {

    }

    @Override
    public void dataReceivedEvent(BasicClient c) {

    }

    @Override
    public void disconnectEvent(BasicClient c) {

    }

    @Override
    public void endOfStreamEvent(BasicClient c) {

    }

    public enum NetworkInstantiationResult {
        SUCCESS, FAILURE, TIMEOUT, ALREADY_CONNECTED
    }
}
