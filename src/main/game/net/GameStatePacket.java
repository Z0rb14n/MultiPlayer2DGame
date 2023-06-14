package game.net;

import game.BallObject;
import game.VehicleObject;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class GameStatePacket implements ByteSerializable {
    static final int MAGIC_NUMBER = 0x71717171;
    private static final VehiclePacket.VehiclePacketFactory vehiclePacketFactory = new VehiclePacket.VehiclePacketFactory();
    private static final BallPacket.BallPacketFactory ballPacketFactory = new BallPacket.BallPacketFactory();

    static {
        registerFactory();
    }

    public static void registerFactory() {
        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new GameStatePacket.GameStatePacketFactory());
    }

    public BallPacket[] balls;
    public VehiclePacket[] vehicles;

    public GameStatePacket(Collection<BallObject> balls, Collection<VehicleObject> vehicles) {
        this(balls.toArray(new BallObject[0]), vehicles.toArray(new VehicleObject[0]));
    }

    public GameStatePacket(BallObject[] balls, VehicleObject[] vehicles) {
        this.balls = new BallPacket[balls.length];
        for (int i = 0; i < balls.length; i++) {
            this.balls[i] = new BallPacket(balls[i]);
        }
        this.vehicles = new VehiclePacket[vehicles.length];
        for (int i = 0; i < vehicles.length; i++) {
            this.vehicles[i] = new VehiclePacket(vehicles[i]);
        }
    }

    public GameStatePacket(BallPacket[] balls, VehiclePacket[] vehicles) {
        this.balls = balls;
        this.vehicles = vehicles;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(balls.length);
        for (BallPacket ball : balls) {
            baos.write(ball.toByteArray(), 0, BallPacket.PACKET_LEN);
        }
        baos.write(vehicles.length);
        for (VehiclePacket vehicle : vehicles) {
            baos.write(vehicle.toByteArray(), 0, VehiclePacket.PACKET_LEN);
        }
        return baos.toByteArray();
    }

    private static class GameStatePacketFactory implements ByteSerializableFactory<GameStatePacket> {
        @Override
        public GameStatePacket deserialize(byte[] data, int index, int len) {
            if (len < 4) return null;
            int ballsLength = ByteSerializable.readInt(index, data);
            BallPacket[] balls = new BallPacket[ballsLength];
            index += 4;
            int remainingLen = len - 4;
            for (int i = 0; i < ballsLength; i++) {
                balls[i] = ballPacketFactory.deserialize(data, index, remainingLen);
                if (balls[i] == null) return null;
                index += BallPacket.PACKET_LEN;
                remainingLen -= BallPacket.PACKET_LEN;
            }
            if (remainingLen < 4) return null;
            int vehiclesLength = ByteSerializable.readInt(index, data);
            VehiclePacket[] vehicles = new VehiclePacket[vehiclesLength];
            index += 4;
            remainingLen -= 4;
            for (int i = 0; i < vehiclesLength; i++) {
                vehicles[i] = vehiclePacketFactory.deserialize(data, index, remainingLen);
                if (vehicles[i] == null) return null;
                index += VehiclePacket.PACKET_LEN;
                remainingLen -= VehiclePacket.PACKET_LEN;
            }
            return new GameStatePacket(balls, vehicles);
        }
    }
}
