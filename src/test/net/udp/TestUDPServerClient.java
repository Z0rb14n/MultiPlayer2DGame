package net.udp;

import net.MagicConstDeserializer;
import net.TestPacket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

public class TestUDPServerClient {
    private static final int PORT = 5204;
    private static final int CLIENT_PORT = 5205;
    private static UDPServer server;
    private static UDPClient client;

    @BeforeAll
    public static void setup() throws IOException {
        server = new UDPServer(PORT);
        client = new UDPClient("localhost", PORT, CLIENT_PORT, 1000);
        assertTrue(server.active());
        assertTrue(client.active());
    }

    @Test
    public void testIO() {
        TestPacket packet = TestPacket.generatePacket();
        client.writePacket(packet);
        awaitUntilTrue(1000, () -> !server.packets.isEmpty());
        TestPacket receivedPacket = (TestPacket) MagicConstDeserializer.deserialize(server.packets.get(0).first, 0, server.packets.get(0).first.length);
        assertEquals(packet, receivedPacket);

        server.writePacket(packet);
        awaitUntilTrue(1000, () -> !client.packets.isEmpty());
        byte[] receivedData = client.packets.remove();
        receivedPacket = (TestPacket) MagicConstDeserializer.deserialize(receivedData, 0, receivedData.length);
        assertEquals(packet, receivedPacket);
    }

    private static void awaitUntilTrue(long timeout, Callable<Boolean> fun) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                if (fun.call()) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fail("Timeout");
    }
}
