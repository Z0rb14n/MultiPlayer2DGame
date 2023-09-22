package game.net;

import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class InitGameInfoPacket implements ByteSerializable {
    static {
        ensureFactoryInitialized();
    }
    private static final int MAGIC_NUMBER = 0xBBBB0000;

    public static void ensureFactoryInitialized() {
        MagicConstDeserializer.registerFactory(InitGameInfoPacket.MAGIC_NUMBER, new InitGameInfoPacket.InitGameInfoPacketFactory());
    }

    private int yourPlayerID;
    private int[] activePlayers;
    private String mapFile;

    public InitGameInfoPacket(int yourPlayerID, int[] activePlayers, String mapFile) {
        this.yourPlayerID = yourPlayerID;
        this.activePlayers = activePlayers;
        this.mapFile = mapFile;
    }

    public int getYourPlayerID() {
        return yourPlayerID;
    }

    public int[] getActivePlayers() {
        return activePlayers;
    }

    public String getMapFile() {
        return mapFile;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        byte[] mapFileBytes = mapFile.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[4 + 4 + mapFileBytes.length + 4 + 4 * activePlayers.length];
        int index = 0;
        ByteSerializable.writeInt(yourPlayerID, bytes, index);
        index += 4;
        ByteSerializable.writeInt(mapFileBytes.length, bytes, index);
        index += 4;
        System.arraycopy(mapFileBytes, 0, bytes, index, mapFileBytes.length);
        index += mapFileBytes.length;
        ByteSerializable.writeInt(activePlayers.length, bytes, index);
        index += 4;
        for (int playerID : activePlayers) {
            ByteSerializable.writeInt(playerID, bytes, index);
            index += 4;
        }
        return bytes;
    }

    public static class InitGameInfoPacketFactory implements ByteSerializableFactory<InitGameInfoPacket> {
        @Override
        public InitGameInfoPacket deserialize(byte[] data, int index, int len) {
            if (len < 8) return null;
            int yourPlayerID = ByteSerializable.readInt(index, data);
            index += 4;
            int mapFileLength = ByteSerializable.readInt(index, data);
            index += 4;
            String mapFile = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data, index, mapFileLength)).toString();
            index += mapFileLength;
            int numPlayers = ByteSerializable.readInt(index, data);
            index += 4;
            if (len < 8 + 4 * numPlayers) return null;
            int[] activePlayers = new int[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                activePlayers[i] = ByteSerializable.readInt(index, data);
                index += 4;
            }
            return new InitGameInfoPacket(yourPlayerID, activePlayers, mapFile);
        }
    }
}
