package game.net;

import game.BallObject;
import game.VehicleObject;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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

    public GameStatePacket(ArrayList<BallObject> balls, ArrayList<VehicleObject> vehicles) {
        this.balls = new BallPacket[balls.size()];
        for (int i = 0; i < balls.size(); i++) {
            this.balls[i] = new BallPacket(balls.get(i));
        }
        this.vehicles = new VehiclePacket[vehicles.size()];
        for (int i = 0; i < vehicles.size(); i++) {
            this.vehicles[i] = new VehiclePacket(vehicles.get(i));
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
            baos.write(ball.toByteArray(), 0, 20);
        }
        baos.write(vehicles.length);
        for (VehiclePacket vehicle : vehicles) {
            baos.write(vehicle.toByteArray(), 0, 20);
        }
        return baos.toByteArray();
    }

    private static class GameStatePacketFactory implements ByteSerializableFactory<GameStatePacket> {
        @Override
        public GameStatePacket deserialize(byte[] data, int index) {
            int ballsLength = ByteSerializable.readInt(index, data);
            BallPacket[] balls = new BallPacket[ballsLength];
            index += 4;
            for (int i = 0; i < ballsLength; i++) {
                balls[i] = ballPacketFactory.deserialize(data, index);
                index += 20;
            }
            int vehiclesLength = ByteSerializable.readInt(index, data);
            VehiclePacket[] vehicles = new VehiclePacket[vehiclesLength];
            index += 4;
            for (int i = 0; i < vehiclesLength; i++) {
                vehicles[i] = vehiclePacketFactory.deserialize(data, index);
                index += 20;
            }
            return new GameStatePacket(balls, vehicles);
        }
    }
}
