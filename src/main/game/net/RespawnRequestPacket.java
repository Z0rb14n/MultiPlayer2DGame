package game.net;

import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

public class RespawnRequestPacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0x43256793;
    static {
        ensureFactoryRegistered();
    }
    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    public static void ensureFactoryRegistered() {
        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new RespawnRequestPacketFactory());
    }

    static class RespawnRequestPacketFactory implements ByteSerializableFactory<RespawnRequestPacket> {
        @Override
        public RespawnRequestPacket deserialize(byte[] data, int index, int len) {
            return new RespawnRequestPacket();
        }
    }
}
