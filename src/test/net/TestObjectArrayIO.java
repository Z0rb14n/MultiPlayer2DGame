package net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TestObjectArrayIO {
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

    private static TestPacket createPacket() {
        Random random = new Random();
        TestPacket packet = new TestPacket();
        //noinspection UnnecessaryUnicodeEscape
        packet.message = "Hello World! \r\n\r\u2603";
        packet.number = random.nextInt();
        packet.bool = random.nextBoolean();
        packet.bytes = new byte[10];
        random.nextBytes(packet.bytes);
        return packet;
    }

    @Test
    public void testBasicClient() {
        TestPacket packet = createPacket();
        TestPacket packet1 = createPacket();
        TestPacket packet2 = createPacket();
        ByteSerializableArray array = new ByteSerializableArray(new ByteSerializable[]{packet1, packet2});
        ByteSerializableArray array1 = new ByteSerializableArray(new ByteSerializable[]{packet, array});
        client.writePacket(array1);
        byte[] bytesArray = array1.toByteArray();
        serverWaitForNumBytesAvailable(bytesArray.length + 8, 3);
        ByteSerializable deserialized = server.available().readPacket();
        assertNotNull(deserialized);
        assertTrue(deserialized instanceof ByteSerializableArray);
        ByteSerializableArray deserializedArray = (ByteSerializableArray) deserialized;
        assertEquals(2, deserializedArray.getArray().length);

        ByteSerializable deserialized1 = deserializedArray.getArray()[0];
        assertNotNull(deserialized1);
        assertTrue(deserialized1 instanceof TestPacket);
        TestPacket deserializedPacket = (TestPacket) deserialized1;
        assertEquals(packet.message, deserializedPacket.message);
        assertEquals(packet.number, deserializedPacket.number);
        assertEquals(packet.bool, deserializedPacket.bool);
        assertArrayEquals(packet.bytes, deserializedPacket.bytes);

        ByteSerializable deserialized2 = deserializedArray.getArray()[1];
        assertNotNull(deserialized2);
        assertTrue(deserialized2 instanceof ByteSerializableArray);
        ByteSerializableArray deserializedArray1 = (ByteSerializableArray) deserialized2;
        assertEquals(2, deserializedArray1.getArray().length);

        ByteSerializable deserialized3 = deserializedArray1.getArray()[0];
        assertNotNull(deserialized3);
        assertTrue(deserialized3 instanceof TestPacket);
        TestPacket deserializedPacket1 = (TestPacket) deserialized3;
        assertEquals(packet1.message, deserializedPacket1.message);
        assertEquals(packet1.number, deserializedPacket1.number);
        assertEquals(packet1.bool, deserializedPacket1.bool);
        assertArrayEquals(packet1.bytes, deserializedPacket1.bytes);

        ByteSerializable deserialized4 = deserializedArray1.getArray()[1];
        assertNotNull(deserialized4);
        assertTrue(deserialized4 instanceof TestPacket);
        TestPacket deserializedPacket2 = (TestPacket) deserialized4;
        assertEquals(packet2.message, deserializedPacket2.message);
        assertEquals(packet2.number, deserializedPacket2.number);
        assertEquals(packet2.bool, deserializedPacket2.bool);
        assertArrayEquals(packet2.bytes, deserializedPacket2.bytes);

        assertNull(server.available());

        server.writePacket(array1);
        byte[] bytesArray1 = array1.toByteArray();
        waitForNumBytesAvailable(bytesArray1.length + 8, 3);
        ByteSerializable deserialized5 = client.readPacket();
        assertNotNull(deserialized5);
        assertTrue(deserialized5 instanceof ByteSerializableArray);
        ByteSerializableArray deserializedArray2 = (ByteSerializableArray) deserialized5;
        assertEquals(2, deserializedArray2.getArray().length);

        ByteSerializable deserialized6 = deserializedArray2.getArray()[0];
        assertNotNull(deserialized6);
        assertTrue(deserialized6 instanceof TestPacket);
        TestPacket deserializedPacket3 = (TestPacket) deserialized6;
        assertEquals(packet.message, deserializedPacket3.message);
        assertEquals(packet.number, deserializedPacket3.number);
        assertEquals(packet.bool, deserializedPacket3.bool);
        assertArrayEquals(packet.bytes, deserializedPacket3.bytes);

        ByteSerializable deserialized7 = deserializedArray2.getArray()[1];
        assertNotNull(deserialized7);
        assertTrue(deserialized7 instanceof ByteSerializableArray);

        ByteSerializableArray deserializedArray3 = (ByteSerializableArray) deserialized7;
        assertEquals(2, deserializedArray3.getArray().length);

        ByteSerializable deserialized8 = deserializedArray3.getArray()[0];
        assertNotNull(deserialized8);
        assertTrue(deserialized8 instanceof TestPacket);

        TestPacket deserializedPacket4 = (TestPacket) deserialized8;
        assertEquals(packet1.message, deserializedPacket4.message);
        assertEquals(packet1.number, deserializedPacket4.number);
        assertEquals(packet1.bool, deserializedPacket4.bool);
        assertArrayEquals(packet1.bytes, deserializedPacket4.bytes);

        ByteSerializable deserialized9 = deserializedArray3.getArray()[1];
        assertNotNull(deserialized9);
        assertTrue(deserialized9 instanceof TestPacket);

        TestPacket deserializedPacket5 = (TestPacket) deserialized9;
        assertEquals(packet2.message, deserializedPacket5.message);
        assertEquals(packet2.number, deserializedPacket5.number);
        assertEquals(packet2.bool, deserializedPacket5.bool);
        assertArrayEquals(packet2.bytes, deserializedPacket5.bytes);
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

    static class TestPacket implements ByteSerializable {
        static {
            MagicConstDeserializer.registerFactory(TestPacket.MAGIC_NUMBER, new TestPacketFactory());
        }
        static final int MAGIC_NUMBER = 0x12345678;
        String message;
        int number;
        boolean bool;
        byte[] bytes;

        public int getMagicNumber() {
            return MAGIC_NUMBER;
        }

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


    static class TestPacketFactory implements ByteSerializableFactory<TestPacket> {
        @Override
        public TestPacket deserialize(byte[] data, int index, int len) {
            if (len < 4) return null;
            TestPacket packet = new TestPacket();
            // read length of str
            int strLength = ByteSerializable.readInt(index, data);
            index += 4;
            // read str
            if (len < 4 + strLength) return null;
            packet.message = new String(data, index, strLength, StandardCharsets.UTF_8);
            index += strLength;
            // read number
            if (len < 4 + strLength + 4) return null;
            packet.number = ByteSerializable.readInt(index, data);
            index += 4;
            // read bool
            if (len < 4 + strLength + 4 + 1) return null;
            packet.bool = data[index++] == 1;
            // read length of bytes
            if (len < 4 + strLength + 4 + 1 + 4) return null;
            int bytesLength = ByteSerializable.readInt(index, data);
            index += 4;
            // read bytes
            if (len < 4 + strLength + 4 + 1 + 4 + bytesLength) return null;
            packet.bytes = new byte[bytesLength];
            System.arraycopy(data, index, packet.bytes, 0, bytesLength);
            return packet;
        }
    }
}
