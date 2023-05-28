package net;

import java.nio.ByteBuffer;

public interface ByteSerializable {

    int getMagicNumber();

    byte[] toByteArray();

    static void writeInt(int value, byte[] bytes, int startIndex) {
        ByteBuffer.wrap(bytes, startIndex, 4).putInt(value);
    }

    static int readInt(int index, byte[] data) {
        return ByteBuffer.wrap(data, index, 4).getInt();
    }
}

