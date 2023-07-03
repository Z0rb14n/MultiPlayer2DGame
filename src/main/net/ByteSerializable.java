package net;

import java.nio.ByteBuffer;

public interface ByteSerializable {

    int getMagicNumber();

    /**
     * Returns the byte array representation of this object.
     * <p></p>
     * Note that the returned byte array does not include magic numbers and byte array length.
     * @return the byte array representation of this object
     */
    byte[] toByteArray();

    static void writeShort(short value, byte[] bytes, int startIndex) {
        ByteBuffer.wrap(bytes, startIndex, 2).putShort(value);
    }

    static void writeInt(int value, byte[] bytes, int startIndex) {
        ByteBuffer.wrap(bytes, startIndex, 4).putInt(value);
    }

    static void writeFloat(float val, byte[] bytes, int startIndex) {
        ByteBuffer.wrap(bytes, startIndex, 4).putFloat(val);
    }

    static int readInt(int index, byte[] data) {
        return ByteBuffer.wrap(data, index, 4).getInt();
    }

    static int readShort(int index, byte[] data) {
        return ByteBuffer.wrap(data, index, 2).getShort();
    }

    static float readFloat(int index, byte[] data) {
        return ByteBuffer.wrap(data, index, 4).getFloat();
    }
}

