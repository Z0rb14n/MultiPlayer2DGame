package net;

import java.nio.charset.StandardCharsets;

public class TestPacketFactory implements ByteSerializableFactory<TestPacket> {
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
