package game.net;

import engine.PhysicsBehaviour;
import game.BallObject;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;
import physics.Vec2D;

public class BallPacket implements ByteSerializable {
    static final int PACKET_LEN = 24;
    static final int MAGIC_NUMBER = 0xAAAA4200;
    static {
        MagicConstDeserializer.registerFactory(BallPacket.MAGIC_NUMBER, new BallPacketFactory());
    }
    private final Vec2D position;

    public Vec2D getPosition() {
        return position;
    }

    public Vec2D getVelocity() {
        return velocity;
    }

    public int getId() {
        return id;
    }

    public int getBounceCount() {
        return bounceCount;
    }

    private final Vec2D velocity;
    private final int id;
    private final int bounceCount;

    public BallPacket(BallObject object) {
        PhysicsBehaviour physics = object.getBehaviour(PhysicsBehaviour.class);
        this.position = object.getPosition();
        this.velocity = physics.getVelocity();
        this.id = object.getId();
        this.bounceCount = object.getBounceCount();
    }

    public BallPacket(Vec2D position, Vec2D velocity, int id, int bounceCount) {
        this.position = position;
        this.velocity = velocity;
        this.id = id;
        this.bounceCount = bounceCount;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[PACKET_LEN];
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
        ByteSerializable.writeInt(bounceCount, bytes, index);
        return bytes;
    }

    static class BallPacketFactory implements ByteSerializableFactory<BallPacket> {
        @Override
        public BallPacket deserialize(byte[] data, int index, int len) {
            if (len < PACKET_LEN) return null;
            Vec2D position = new Vec2D(ByteSerializable.readFloat(index, data), ByteSerializable.readFloat(index + 4, data));
            Vec2D velocity = new Vec2D(ByteSerializable.readFloat(index + 8, data), ByteSerializable.readFloat(index + 12, data));
            int id = ByteSerializable.readInt(index + 16, data);
            int bounceCount = ByteSerializable.readInt(index + 20, data);
            return new BallPacket(position, velocity, id, bounceCount);
        }
    }
}
