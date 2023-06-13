package game.net;

import engine.PhysicsBehaviour;
import game.VehicleObject;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;
import physics.Vec2D;
import physics.shape.RotatedTriangle;

public class VehiclePacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0xAAAA6969;
    static {
        MagicConstDeserializer.registerFactory(VehiclePacket.MAGIC_NUMBER, new VehiclePacket.VehiclePacketFactory());
    }
    private final Vec2D position;
    private final Vec2D velocity;
    private final int id;
    private final float angle;

    public VehiclePacket(VehicleObject object) {
        PhysicsBehaviour physics = object.getBehaviour(PhysicsBehaviour.class);
        this.position = object.getPosition();
        this.velocity = physics.getVelocity();
        this.id = object.getId();
        this.angle = ((RotatedTriangle) physics.getTranslatedShape()).getAngle();
    }

    public VehiclePacket(Vec2D position, Vec2D velocity, int id, float angle) {
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
        ByteSerializable.writeInt(id, bytes, index);
        index += 4;
        ByteSerializable.writeFloat(angle, bytes, index);
        return bytes;
    }

    static class VehiclePacketFactory implements ByteSerializableFactory<VehiclePacket> {
        @Override
        public VehiclePacket deserialize(byte[] data, int index) {
            Vec2D position = new Vec2D(ByteSerializable.readFloat(index, data), ByteSerializable.readFloat(index + 4, data));
            Vec2D velocity = new Vec2D(ByteSerializable.readFloat(index + 8, data), ByteSerializable.readFloat(index + 12, data));
            int id = ByteSerializable.readInt(index + 16, data);
            float angle = ByteSerializable.readFloat(index + 20, data);
            return new VehiclePacket(position, velocity, id, angle);
        }
    }
}
