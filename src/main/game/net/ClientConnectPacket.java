package game.net;

import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

import java.nio.charset.StandardCharsets;

public class ClientConnectPacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0x5234783A;
    private final String username;

    static {
        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new ClientConnectPacketFactory());
    }

    public ClientConnectPacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        return username.getBytes(StandardCharsets.UTF_8);
    }

    static class ClientConnectPacketFactory implements ByteSerializableFactory<ClientConnectPacket> {
        @Override
        public ClientConnectPacket deserialize(byte[] data, int index, int len) {
            try {
                String str = new String(data, index, len, StandardCharsets.UTF_8);
                return new ClientConnectPacket(str);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
