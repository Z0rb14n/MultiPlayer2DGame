package game;

import game.net.ClientConnectPacket;
import game.net.GameStatePacket;
import game.net.InitGameInfoPacket;
import game.net.NetworkConstants;
import net.ByteSerializable;
import net.udp.UDPClient;
import net.udp.UDPClientNetworkEventReceiver;
import ui.client.GameFrame;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

public class GameClientController implements UDPClientNetworkEventReceiver {
    private static GameClientController singleton;

    private GameClientController() {
    }

    public static GameClientController getInstance() {
        if (singleton == null) singleton = new GameClientController();
        return singleton;
    }

    private UDPClient client;
    private int playerNumber;
    private final ArrayList<Integer> currentPlayers = new ArrayList<>();
    public NetworkInstantiationResult connect(String ip) {
        try {
            GameLogger.getDefault().log("Starting instantiation of client...", "NETWORK");
            client = new UDPClient(ip, NetworkConstants.PORT);
            GameLogger.getDefault().log("Client instantiated.", "NETWORK");
            client.writePacket(new ClientConnectPacket("funny"));
            InitGameInfoPacket.ensureFactoryInitialized();
            GameStatePacket.registerFactory();
            ByteSerializable packet = spinReadPacket(500);
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
            client.addNetworkEventReceiver(this);
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
        }
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public VehicleObject getVehicleObject() {
        return GameController.getInstance().getVehicle(playerNumber);
    }

    private ByteSerializable spinReadPacket(int retries) {
        return spinReadPacket(retries, 100);
    }
    private ByteSerializable spinReadPacket(int retries, long sleep) {
        for (int i = 0; i < retries; i++) {
            ByteSerializable packet = client.readPacket();
            if (packet != null) return packet;
            try {
                Thread.sleep(sleep);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void sendPacket(ByteSerializable packet) {
        client.writePacket(packet);
    }

    @Override
    public void dataReceivedEvent(UDPClient c, byte[] data) {
        GameLogger.getDefault().log("Client::dataReceivedEvent", "NETWORK");
        ByteSerializable packet = c.readPacket();
        if (packet == null) return;
        GameLogger.getDefault().log("Packet received: " + packet, "NETWORK");
        if (packet instanceof GameStatePacket) {
            GameStatePacket gameStatePacket = (GameStatePacket) packet;
            GameFrame.getInstance().updatePacket(gameStatePacket);
        }
    }

    /*

    @Override
    public void disconnectEvent(NetworkClient c) {
        System.out.println("Client::disconnectEvent");
        ArrayList<ByteSerializable> packets = new ArrayList<>();
        ByteSerializable packet;
        while ((packet = c.readPacket()) != null) {
            packets.add(packet);
        }
        System.out.println("Packets received on disconnect: " + packets.size());

        for (ByteSerializable p : packets) {
            if (p instanceof GameStatePacket) {
                GameStatePacket gameStatePacket = (GameStatePacket) p;
                GameController.getInstance().updateFromPacket(gameStatePacket);
                GameFrame.getInstance().updatePacket(gameStatePacket);
            }
        }
        GameFrame.getInstance().endGame();
    }

    @Override
    public void endOfStreamEvent(NetworkClient c) {
        System.out.println("Client::endOfStreamEvent");
        GameFrame.getInstance().endGame();
    }
    */

    public enum NetworkInstantiationResult {
        SUCCESS, FAILURE, TIMEOUT, ALREADY_CONNECTED
    }
}
