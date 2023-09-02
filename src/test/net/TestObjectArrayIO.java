package net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class TestObjectArrayIO {
    private static final int PORT = 5204;
    private static TCPServer server;
    private static TCPClient client;

    @BeforeAll
    public static void setup() throws IOException {
        server = new TCPServer(PORT);
        client = new TCPClient("localhost", PORT);
        assertTrue(server.active());
        assertTrue(client.active());
    }


    @Test
    public void testBasicClient() {
        TestPacket packet = TestPacket.generatePacket();
        TestPacket packet1 = TestPacket.generatePacket();
        TestPacket packet2 = TestPacket.generatePacket();
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
        assertEquals(packet, deserializedPacket);

        ByteSerializable deserialized2 = deserializedArray.getArray()[1];
        assertNotNull(deserialized2);
        assertTrue(deserialized2 instanceof ByteSerializableArray);
        ByteSerializableArray deserializedArray1 = (ByteSerializableArray) deserialized2;
        assertEquals(2, deserializedArray1.getArray().length);

        ByteSerializable deserialized3 = deserializedArray1.getArray()[0];
        assertNotNull(deserialized3);
        assertTrue(deserialized3 instanceof TestPacket);
        TestPacket deserializedPacket1 = (TestPacket) deserialized3;
        assertEquals(packet1, deserializedPacket1);

        ByteSerializable deserialized4 = deserializedArray1.getArray()[1];
        assertNotNull(deserialized4);
        assertTrue(deserialized4 instanceof TestPacket);
        TestPacket deserializedPacket2 = (TestPacket) deserialized4;
        assertEquals(packet2, deserializedPacket2);


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
        assertEquals(packet, deserializedPacket3);

        ByteSerializable deserialized7 = deserializedArray2.getArray()[1];
        assertNotNull(deserialized7);
        assertTrue(deserialized7 instanceof ByteSerializableArray);

        ByteSerializableArray deserializedArray3 = (ByteSerializableArray) deserialized7;
        assertEquals(2, deserializedArray3.getArray().length);

        ByteSerializable deserialized8 = deserializedArray3.getArray()[0];
        assertNotNull(deserialized8);
        assertTrue(deserialized8 instanceof TestPacket);

        TestPacket deserializedPacket4 = (TestPacket) deserialized8;
        assertEquals(packet1, deserializedPacket4);

        ByteSerializable deserialized9 = deserializedArray3.getArray()[1];
        assertNotNull(deserialized9);
        assertTrue(deserialized9 instanceof TestPacket);

        TestPacket deserializedPacket5 = (TestPacket) deserialized9;
        assertEquals(packet2, deserializedPacket5);
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
