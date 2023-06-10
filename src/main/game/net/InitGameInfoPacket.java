package game.net;

import game.PlayerID;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

public class InitGameInfoPacket implements ByteSerializable {
    static {
        MagicConstDeserializer.registerFactory(InitGameInfoPacket.MAGIC_NUMBER, new InitGameInfoPacket.InitGameInfoPacketFactory());
    }
    private static final int MAGIC_NUMBER = 0xBBBB0000;
    private PlayerID yourPlayerID;
    private PlayerID[] activePlayers;

    public InitGameInfoPacket(PlayerID yourPlayerID, PlayerID[] activePlayers) {
        this.yourPlayerID = yourPlayerID;
        this.activePlayers = activePlayers;
    }

    public PlayerID getYourPlayerID() {
        return yourPlayerID;
    }

    public PlayerID[] getActivePlayers() {
        return activePlayers;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[4 + 4 + 4 * activePlayers.length];
        int index = 0;
        ByteSerializable.writeInt(yourPlayerID.getID(), bytes, index);
        index += 4;
        ByteSerializable.writeInt(activePlayers.length, bytes, index);
        index += 4;
        for (PlayerID playerID : activePlayers) {
            ByteSerializable.writeInt(playerID.getID(), bytes, index);
            index += 4;
        }
        return bytes;
    }

    public static class InitGameInfoPacketFactory implements ByteSerializableFactory<InitGameInfoPacket> {
        @Override
        public InitGameInfoPacket deserialize(byte[] data, int index) {
            PlayerID yourPlayerID = new PlayerID(ByteSerializable.readInt(index, data));
            index += 4;
            int numPlayers = ByteSerializable.readInt(index, data);
            index += 4;
            PlayerID[] activePlayers = new PlayerID[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                activePlayers[i] = new PlayerID(ByteSerializable.readInt(index, data));
                index += 4;
            }
            return new InitGameInfoPacket(yourPlayerID, activePlayers);
        }
    }
}
