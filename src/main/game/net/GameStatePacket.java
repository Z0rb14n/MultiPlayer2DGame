package game.net;

import game.BallObject;
import game.VehicleObject;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

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
        byte[] bytes = new byte[2 + balls.length * BallPacket.PACKET_LEN + 2 + vehicles.length * VehiclePacket.PACKET_LEN];
        ByteSerializable.writeShort((short) balls.length, bytes, 0);
        int offset = 2;
        for (BallPacket ball : balls) {
            ball.writeToArray(bytes, offset);
            offset += BallPacket.PACKET_LEN;
        }
        ByteSerializable.writeShort((short) vehicles.length, bytes, offset);
        offset += 2;
        for (VehiclePacket vehicle : vehicles) {
            vehicle.writeToArray(bytes, offset);
            offset += VehiclePacket.PACKET_LEN;
        }
        return bytes;
    }

    private static class GameStatePacketFactory implements ByteSerializableFactory<GameStatePacket> {
        @Override
        public GameStatePacket deserialize(byte[] data, int index, int len) {
            if (len < 2) return null;
            int ballsLength = ByteSerializable.readShort(index, data);
            if (ballsLength < 0) System.err.println("Invalid balls length: " + ballsLength);
            //if (ballsLength > 100) System.out.println("Prepare for Heap Space error: " + ballsLength);
            BallPacket[] balls = new BallPacket[ballsLength];
            index += 2;
            int remainingLen = len - 2;
            for (int i = 0; i < ballsLength; i++) {
                balls[i] = ballPacketFactory.deserialize(data, index, remainingLen);
                if (balls[i] == null) return null;
                index += BallPacket.PACKET_LEN;
                remainingLen -= BallPacket.PACKET_LEN;
            }
            if (remainingLen < 2) return null;
            int vehiclesLength = ByteSerializable.readShort(index, data);
            if (vehiclesLength > 100) System.out.println("Prepare for Heap Space error: " + vehiclesLength);
            VehiclePacket[] vehicles = new VehiclePacket[vehiclesLength];
            index += 2;
            remainingLen -= 2;
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
