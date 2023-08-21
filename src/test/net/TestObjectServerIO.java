package net;

import game.net.BallPacket;
import game.net.GameStatePacket;
import game.net.InputPacket;
import game.net.VehiclePacket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import physics.Vec2D;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TestObjectServerIO {
    private static final int PORT = 5204;
    private static BasicServer server;
    private static BasicClient client;

    @BeforeAll
    public static void setup() throws IOException {
        server = new BasicServer(PORT);
        client = new BasicClient("localhost", PORT);
        assertTrue(server.active());
        assertTrue(client.active());
    }

    @Test
    public void testClientToServer() {
        TestPacket packet = TestPacket.generatePacket();
        byte[] bytesArray = packet.toByteArray();
        client.writeInt(bytesArray.length);
        client.writeInt(TestPacket.MAGIC_NUMBER);
        client.writeBytes(bytesArray);

        serverWaitForNumBytesAvailable(bytesArray.length + 8, 3);
        int length = server.available().readInt();
        assertEquals(bytesArray.length, length);
        byte[] returnedBytes = server.available().readBytes(length+4);
        ByteSerializable deserialized = MagicConstDeserializer.deserialize(returnedBytes, 0, returnedBytes.length);

        assertNotNull(deserialized);
        assertInstanceOf(TestPacket.class, deserialized);
        TestPacket deserializedPacket = (TestPacket) deserialized;
        assertEquals(packet, deserializedPacket);
    }

    @Test
    public void testBasicClient() {
        TestPacket packet = TestPacket.generatePacket();
        byte[] bytesArray = packet.toByteArray();
        client.writePacket(packet);
        serverWaitForNumBytesAvailable(bytesArray.length + 8, 3);
        ByteSerializable deserialized = server.available().readPacket();
        assertNotNull(deserialized);
        assertInstanceOf(TestPacket.class, deserialized);
        TestPacket deserializedPacket = (TestPacket) deserialized;
        assertEquals(packet, deserializedPacket);
    }

    @Test
    public void testServerToClient() {
        TestPacket packet = TestPacket.generatePacket();
        byte[] bytesArray = packet.toByteArray();
        server.writeInt(bytesArray.length);
        server.writeInt(TestPacket.MAGIC_NUMBER);
        server.writeBytes(bytesArray);

        waitForNumBytesAvailable(bytesArray.length + 8, 3);
        int length = client.readInt();
        assertEquals(bytesArray.length, length);
        byte[] returnedBytes = client.readBytes(length+4);

        MagicConstDeserializer.registerFactory(TestPacket.MAGIC_NUMBER, new TestPacketFactory());
        ByteSerializable deserialized = MagicConstDeserializer.deserialize(returnedBytes, 0, returnedBytes.length);

        assertNotNull(deserialized);
        assertInstanceOf(TestPacket.class, deserialized);
        TestPacket deserializedPacket = (TestPacket) deserialized;
        assertEquals(packet, deserializedPacket);
    }

    @Test
    public void testSendMultipleReadMultiple() {
        // generate 20 random balls and vehicle packets
        BallPacket[] balls = new BallPacket[20];
        VehiclePacket[] vehicles = new VehiclePacket[20];
        for (int i = 0; i < 20; i++) {
            balls[i] = randomBallPacket();
            vehicles[i] = randomVehiclePacket();
        }
        GameStatePacket packet = new GameStatePacket(balls, vehicles);
        assertEquals(0, client.available());
        server.writePacket(packet);
        server.writePacket(packet);
        server.writePacket(packet);
        GameStatePacket readPacket = (GameStatePacket) client.readPacket();
        GameStatePacket readPacket2 = (GameStatePacket) client.readPacket();
        GameStatePacket readPacket3 = (GameStatePacket) client.readPacket();
        assertGamePacketEquals(packet, readPacket);
        assertGamePacketEquals(packet, readPacket2);
        assertGamePacketEquals(packet, readPacket3);
        assertEquals(0, client.available());
    }

    @Test
    public void testSendInputPacket() {
        // create and send 3 input packets
        InputPacket[] packets = new InputPacket[3];
        for (int i = 0; i < 3; i++) {
            packets[i] = InputPacket.EMPTY;
            server.writePacket(packets[i]);
        }
        // read 3 input packets
        for (int i = 0; i < 3; i++) {
            InputPacket readPacket = (InputPacket) client.readPacket();
            assertInputPacketEquals(packets[i], readPacket);
        }
    }

    private static void assertInputPacketEquals(InputPacket packet, InputPacket readPacket) {
        // two array equals
        assertArrayEquals(packet.getInput(), readPacket.getInput());
        assertArrayEquals(packet.getPressed(), readPacket.getPressed());
    }

    private static void assertBallPacketEquals(BallPacket p1, BallPacket p2) {
        assertEquals(p1.getPosition(), p2.getPosition());
        assertEquals(p1.getVelocity(), p2.getVelocity());
        assertEquals(p1.getId(), p2.getId());
        assertEquals(p1.getBounceCount(), p2.getBounceCount());
    }

    private static void assertVehiclePacketEquals(VehiclePacket p1, VehiclePacket p2) {
        assertEquals(p1.getPosition(), p2.getPosition());
        assertEquals(p1.getVelocity(), p2.getVelocity());
        assertEquals(p1.getAngle(), p2.getAngle());
        assertEquals(p1.getId(), p2.getId());
    }

    private static void assertGamePacketEquals(GameStatePacket p1, GameStatePacket p2) {
        assertEquals(p1.balls.length, p2.balls.length);
        assertEquals(p1.vehicles.length, p2.vehicles.length);
        for (int i = 0; i < p1.balls.length; i++) {
            assertBallPacketEquals(p1.balls[i], p2.balls[i]);
        }
        for (int i = 0; i < p1.vehicles.length; i++) {
            assertVehiclePacketEquals(p1.vehicles[i], p2.vehicles[i]);
        }
    }

    private static BallPacket randomBallPacket() {
        Random random = new Random();
        Vec2D pos = new Vec2D(random.nextFloat(), random.nextFloat());
        Vec2D vel = new Vec2D(random.nextFloat(), random.nextFloat());
        int id = random.nextInt();
        int bounceCount = random.nextInt();
        return new BallPacket(pos, vel, id, bounceCount);
    }

    private static VehiclePacket randomVehiclePacket() {
        Random random = new Random();
        Vec2D pos = new Vec2D(random.nextFloat(), random.nextFloat());
        Vec2D vel = new Vec2D(random.nextFloat(), random.nextFloat());
        int id = random.nextInt();
        float angle = random.nextFloat();
        return new VehiclePacket(pos, vel, id, angle);
    }

    private static void waitForNumBytesAvailable(int num, int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            if (client.available() == num) return;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fail();
    }

    private static void serverWaitForNumBytesAvailable(int num, int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            if (server.available() != null) {
                if (server.available().available() == num) return;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fail();
    }

    @AfterAll
    public static void teardown() {
        client.stop();
        server.dispose();
    }
}
