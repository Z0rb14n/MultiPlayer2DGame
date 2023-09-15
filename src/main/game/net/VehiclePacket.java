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
    static final int PACKET_LEN = 25;
    static {
        MagicConstDeserializer.registerFactory(VehiclePacket.MAGIC_NUMBER, new VehiclePacket.VehiclePacketFactory());
    }
    private final Vec2D position;

    private final Vec2D velocity;
    private final int id;
    private final float angle;
    private final boolean dead;

    public VehiclePacket(VehicleObject object) {
        PhysicsBehaviour physics = object.getBehaviour(PhysicsBehaviour.class);
        this.position = object.getPosition();
        this.velocity = physics.getVelocity();
        this.id = object.getId();
        this.angle = ((RotatedTriangle) physics.getTranslatedShape()).getAngle();
        this.dead = object.isDead();
    }

    public VehiclePacket(Vec2D position, Vec2D velocity, int id, float angle, boolean isDead) {
        this.position = position;
        this.velocity = velocity;
        this.id = id;
        this.angle = angle;
        this.dead = isDead;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    public Vec2D getPosition() {
        return position;
    }

    public Vec2D getVelocity() {
        return velocity;
    }

    public int getId() {
        return id;
    }

    public float getAngle() {
        return angle;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[PACKET_LEN];
        writeToArray(bytes, 0);
        return bytes;
    }

    public void writeToArray(byte[] array, int offset) {
        ByteSerializable.writeFloat(position.getX(), array, offset);
        offset += 4;
        ByteSerializable.writeFloat(position.getY(), array, offset);
        offset += 4;
        ByteSerializable.writeFloat(velocity.getX(), array, offset);
        offset += 4;
        ByteSerializable.writeFloat(velocity.getY(), array, offset);
        offset += 4;
        ByteSerializable.writeInt(id, array, offset);
        offset += 4;
        ByteSerializable.writeFloat(angle, array, offset);
        offset += 4;
        array[offset] = (byte) (dead ? 1 : 0);
    }

    static class VehiclePacketFactory implements ByteSerializableFactory<VehiclePacket> {
        @Override
        public VehiclePacket deserialize(byte[] data, int index, int len) {
            if (len < PACKET_LEN) return null;
            Vec2D position = new Vec2D(ByteSerializable.readFloat(index, data), ByteSerializable.readFloat(index + 4, data));
            Vec2D velocity = new Vec2D(ByteSerializable.readFloat(index + 8, data), ByteSerializable.readFloat(index + 12, data));
            int id = ByteSerializable.readInt(index + 16, data);
            float angle = ByteSerializable.readFloat(index + 20, data);
            boolean dead = data[index + 24] == 1;
            return new VehiclePacket(position, velocity, id, angle, dead);
        }
    }
}
