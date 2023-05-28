package net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestBasicServerIO {
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
    public void testIO() {
        Random random = new Random();
        for (int tries = 0; tries < 10; tries++) {
            byte b = (byte) random.nextInt();
            server.writeByte(b);
            waitForNumBytesAvailable(1,3);
            assertEquals(b, client.read());

            client.writeByte(b);
            serverWaitForNumBytesAvailable(1,3);
            assertEquals(b, server.available().read());

            short s = (short) random.nextInt();
            server.writeShort(s);
            waitForNumBytesAvailable(2,3);
            assertEquals(s, client.readShort());

            client.writeShort(s);
            serverWaitForNumBytesAvailable(2,3);
            assertEquals(s, server.available().readShort());

            int i = random.nextInt();
            server.writeInt(i);
            waitForNumBytesAvailable(4,3);
            assertEquals(i, client.readInt());

            client.writeInt(i);
            serverWaitForNumBytesAvailable(4,3);
            assertEquals(i, server.available().readInt());

            long l = random.nextLong();
            server.writeLong(l);
            waitForNumBytesAvailable(8,3);
            assertEquals(l, client.readLong());

            client.writeLong(l);
            serverWaitForNumBytesAvailable(8,3);
            assertEquals(l, server.available().readLong());

            float f = random.nextFloat();
            server.writeFloat(f);
            waitForNumBytesAvailable(4,3);
            assertEquals(f, client.readFloat());

            client.writeFloat(f);
            serverWaitForNumBytesAvailable(4,3);
            assertEquals(f, server.available().readFloat());
        }

        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        server.writeBytes(bytes);
        waitForNumBytesAvailable(10,3);
        assertArrayEquals(bytes, client.readBytes(10));

        client.writeBytes(bytes);
        serverWaitForNumBytesAvailable(10,3);
        assertArrayEquals(bytes, server.available().readBytes(10));

        @SuppressWarnings("UnnecessaryUnicodeEscape") String str = "helloWorld! \n\r\n\u2603";
        server.writeStr(str);
        waitForNumBytesAvailable(str.getBytes(StandardCharsets.UTF_8).length,3);
        assertEquals(str, client.readString());

        client.writeStr(str);
        serverWaitForNumBytesAvailable(str.getBytes(StandardCharsets.UTF_8).length,3);
        assertEquals(str, server.available().readString());

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
        server.stop();
    }
}
