package net;

import game.PlayerID;
import physics.Vec2D;

public class VehiclePacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0xAAAA6969;
    static {
        MagicConstDeserializer.registerFactory(VehiclePacket.MAGIC_NUMBER, new VehiclePacket.VehiclePacketFactory());
    }
    private final Vec2D position;
    private final Vec2D velocity;
    private final PlayerID id;
    private final float angle;

    public VehiclePacket(Vec2D position, Vec2D velocity, PlayerID id, float angle) {
        this.position = position;
        this.velocity = velocity;
        this.id = id;
        this.angle = angle;
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
        index += 4;
        ByteSerializable.writeFloat(angle, bytes, index);
        return bytes;
    }

    private static class VehiclePacketFactory implements ByteSerializableFactory<VehiclePacket> {
        @Override
        public VehiclePacket deserialize(byte[] data, int index) {
            Vec2D position = new Vec2D(ByteSerializable.readFloat(index, data), ByteSerializable.readFloat(index + 4, data));
            Vec2D velocity = new Vec2D(ByteSerializable.readFloat(index + 8, data), ByteSerializable.readFloat(index + 12, data));
            PlayerID id = new PlayerID(ByteSerializable.readInt(index + 16, data));
            float angle = ByteSerializable.readFloat(index + 20, data);
            return new VehiclePacket(position, velocity, id, angle);
        }
    }
}
