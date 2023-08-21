package net;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class TestPacket implements ByteSerializable {
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

    public static TestPacket generatePacket() {
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

    public boolean equals(Object o) {
        if (!(o instanceof TestPacket)) return false;
        TestPacket other = (TestPacket) o;
        return message.equals(other.message) && number == other.number && bool == other.bool && Arrays.equals(bytes, other.bytes);
    }
}
