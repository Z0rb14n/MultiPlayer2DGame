package net;

import java.nio.ByteBuffer;

/**
 * A ByteSerializableArray is a ByteSerializable that contains an array of ByteSerializables.
 *
 * The format of the data is as follows:
 * [length][sublength][magic][data][sublength][magic][data]...
 */
public class ByteSerializableArray implements ByteSerializable {
    static {
        MagicConstDeserializer.registerFactory(ByteSerializableArray.MAGIC_NUMBER, new ByteSerializableArrayFactory());
    }
    static final int MAGIC_NUMBER = 0xAAAA0000;
    private final ByteSerializable[] array;

    public ByteSerializableArray(ByteSerializable[] array) {
        this.array = array;
    }

    public ByteSerializable[] getArray() {
        return array;
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        int buffersize = 4;
        for (int i = 0; i < array.length; i++) {
            buffersize += 4 + 4 + array[i].toByteArray().length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(buffersize).putInt(array.length);
        for (int i = 0; i < array.length; i++) {
            byte[] bytes = array[i].toByteArray();
            buffer.putInt(bytes.length);
            buffer.putInt(array[i].getMagicNumber());
            buffer.put(bytes);
        }
        return buffer.array();
    }

    public static ByteSerializableArray deserialize(byte[] data, int offset, int len) {
        if (len < 4) return null;
        int length = ByteSerializable.readInt(offset, data);
        int totalUsed = 4;
        offset += 4;
        ByteSerializable[] array = new ByteSerializable[length];
        for (int i = 0; i < length; i++) {
            if (totalUsed+4 > len) return null;
            int sublength = ByteSerializable.readInt(offset, data);
            offset += 4;
            totalUsed +=4;
            if (totalUsed+ sublength+4 > len) return null;
            totalUsed += sublength + 4;
            array[i] = MagicConstDeserializer.deserialize(data, offset, sublength);
            if (array[i] == null) return null;
            offset += sublength + 4;
        }
        return new ByteSerializableArray(array);
    }

    private static class ByteSerializableArrayFactory implements ByteSerializableFactory<ByteSerializableArray> {
        @Override
        public ByteSerializableArray deserialize(byte[] data, int offset, int len) {
            return ByteSerializableArray.deserialize(data, offset, len);
        }
    }
}
