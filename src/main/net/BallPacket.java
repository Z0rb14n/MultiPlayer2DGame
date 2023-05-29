package net;

import game.PlayerID;
import physics.Vec2D;

public class BallPacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0xAAAA4200;
    static {
        MagicConstDeserializer.registerFactory(BallPacket.MAGIC_NUMBER, new BallPacketFactory());
    }
    private final Vec2D position;
    private final Vec2D velocity;
    private final PlayerID id;

    public BallPacket(Vec2D position, Vec2D velocity, PlayerID id) {
        this.position = position;
        this.velocity = velocity;
        this.id = id;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[20];
        int index = 0;
        ByteSerializable.writeFloat(position.getX(), bytes, index);
        index += 4;
        ByteSerializable.writeFloat(position.getY(), bytes, index);
        index += 4;
        ByteSerializable.writeFloat(velocity.getX(), bytes, index);
        index += 4;
        ByteSerializable.writeFloat(velocity.getY(), bytes, index);
        index += 4;
        ByteSerializable.writeInt(id.getID(), bytes, index);
        return bytes;
    }

    private static class BallPacketFactory implements ByteSerializableFactory<BallPacket> {
        @Override
        public BallPacket deserialize(byte[] data, int index) {
            Vec2D position = new Vec2D(ByteSerializable.readFloat(index, data), ByteSerializable.readFloat(index + 4, data));
            Vec2D velocity = new Vec2D(ByteSerializable.readFloat(index + 8, data), ByteSerializable.readFloat(index + 12, data));
            PlayerID id = new PlayerID(ByteSerializable.readInt(index + 16, data));
            return new BallPacket(position, velocity, id);
        }
    }
}
