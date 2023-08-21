package net.udp;

import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestUDP {
    private static final int PORT = 5204;
    private static final int CLIENT_PORT = 5205;
    private static DatagramSocket server;
    private static DatagramSocket client;

    @BeforeAll
    public static void setup() throws IOException {
        server = new DatagramSocket(PORT);
        client = new DatagramSocket(CLIENT_PORT);
        client.setSoTimeout(100);
        server.setSoTimeout(100);
    }

    @Test
    public void testIO() throws IOException {
        for (int tries = 0; tries < 10; tries++) {
            byte[] data = generatePacket().toByteArray();
            DatagramPacket sentPacket = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), PORT);
            client.send(sentPacket);
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            server.receive(receivedPacket);
            assertEquals(data.length, receivedPacket.getLength());
            for (int i = 0; i < data.length; i++) {
                assertEquals(data[i], receivedPacket.getData()[i]);
            }
        }

        for (int tries = 0; tries < 10; tries++) {
            byte[] data = generatePacket().toByteArray();

            DatagramPacket sentPacket = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), CLIENT_PORT);
            server.send(sentPacket);
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            client.receive(receivedPacket);
            assertEquals(data.length, receivedPacket.getLength());
            for (int i = 0; i < data.length; i++) {
                assertEquals(data[i], receivedPacket.getData()[i]);
            }
        }
    }

    @AfterAll
    public static void teardown() {
        client.close();
        server.close();
    }

    private static TestPacket generatePacket() {
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
            TestPacket packet = new TestPacket();
            // read length of str
            if (len < 4) return null;
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
