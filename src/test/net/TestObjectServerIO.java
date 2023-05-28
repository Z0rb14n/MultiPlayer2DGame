package net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TestObjectServerIO {
    private static final int MAGIC_NUMBER = 0x12345678;
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
        Random random = new Random();
        TestPacket packet = new TestPacket();
        packet.message = "Hello World! \r\n\r\u2603";
        packet.number = random.nextInt();
        packet.bool = random.nextBoolean();
        packet.bytes = new byte[10];
        random.nextBytes(packet.bytes);
        byte[] bytesArray = packet.toByteArray();
        client.writeInt(bytesArray.length);
        client.writeInt(MAGIC_NUMBER);
        client.writeBytes(bytesArray);

        serverWaitForNumBytesAvailable(bytesArray.length + 8, 3);
        int length = server.available().readInt();
        assertEquals(bytesArray.length, length);
        byte[] returnedBytes = server.available().readBytes(length+4);

        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new TestPacketFactory());
        ByteSerializable deserialized = MagicConstDeserializer.deserialize(returnedBytes, 0);

        assertNotNull(deserialized);
        assertInstanceOf(TestPacket.class, deserialized);
        TestPacket deserializedPacket = (TestPacket) deserialized;

        assertEquals(packet.message, deserializedPacket.message);
        assertEquals(packet.number, deserializedPacket.number);
        assertEquals(packet.bool, deserializedPacket.bool);
        assertArrayEquals(packet.bytes, deserializedPacket.bytes);
    }

    @Test
    public void testServerToClient() {
        Random random = new Random();
        TestPacket packet = new TestPacket();
        packet.message = "Hello World! \r\n\r\u2603";
        packet.number = random.nextInt();
        packet.bool = random.nextBoolean();
        packet.bytes = new byte[10];
        random.nextBytes(packet.bytes);
        byte[] bytesArray = packet.toByteArray();
        server.writeInt(bytesArray.length);
        server.writeInt(MAGIC_NUMBER);
        server.writeBytes(bytesArray);

        waitForNumBytesAvailable(bytesArray.length + 8, 3);
        int length = client.readInt();
        assertEquals(bytesArray.length, length);
        byte[] returnedBytes = client.readBytes(length+4);

        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new TestPacketFactory());
        ByteSerializable deserialized = MagicConstDeserializer.deserialize(returnedBytes, 0);

        assertNotNull(deserialized);
        assertInstanceOf(TestPacket.class, deserialized);
        TestPacket deserializedPacket = (TestPacket) deserialized;

        assertEquals(packet.message, deserializedPacket.message);
        assertEquals(packet.number, deserializedPacket.number);
        assertEquals(packet.bool, deserializedPacket.bool);
        assertArrayEquals(packet.bytes, deserializedPacket.bytes);
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

    private static class TestPacket implements ByteSerializable {
        private String message;
        private int number;
        private boolean bool;
        private byte[] bytes;

        @Override
        public byte[] toByteArray() {
            byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);
            // remember to include length of str/bytes
            int totalLength = 4 + byteMessage.length + 4 + 1 + 4 + bytes.length;
            byte[] data = new byte[totalLength];
            int index = 0;
            // write length of str/bytes
            ByteSerializable.writeInt(byteMessage.length, data, index);
            index += 4;
            // write str/bytes
            System.arraycopy(byteMessage, 0, data, index, byteMessage.length);
            index += byteMessage.length;
            // write number
            ByteSerializable.writeInt(number, data, index);
            index += 4;
            // write bool
            data[index++] = (byte) (bool ? 1 : 0);
            // write length of bytes
            ByteSerializable.writeInt(bytes.length, data, index);
            index += 4;
            // write bytes
            System.arraycopy(bytes, 0, data, index, bytes.length);
            return data;
        }
    }

    private static class TestPacketFactory implements ByteSerializableFactory<TestPacket> {
        @Override
        public TestPacket deserialize(byte[] data, int index) {
            TestPacket packet = new TestPacket();
            // read length of str
            int strLength = ByteSerializable.readInt(index, data);
            index += 4;
            // read str
            packet.message = new String(data, index, strLength, StandardCharsets.UTF_8);
            index += strLength;
            // read number
            packet.number = ByteSerializable.readInt(index, data);
            index += 4;
            // read bool
            packet.bool = data[index++] == 1;
            // read length of bytes
            int bytesLength = ByteSerializable.readInt(index, data);
            index += 4;
            // read bytes
            packet.bytes = new byte[bytesLength];
            System.arraycopy(data, index, packet.bytes, 0, bytesLength);
            return packet;
        }
    }
}
