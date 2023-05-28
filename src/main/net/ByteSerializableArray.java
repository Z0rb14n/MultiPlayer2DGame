package net;

import java.nio.ByteBuffer;

// of format [length][sublength][magic][data][sublength][magic][data]...
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

    public static ByteSerializableArray deserialize(byte[] data, int offset) {
        int length = ByteSerializable.readInt(offset, data);
        offset += 4;
        ByteSerializable[] array = new ByteSerializable[length];
        for (int i = 0; i < length; i++) {
            int sublength = ByteSerializable.readInt(offset, data);
            offset += 4;
            array[i] = MagicConstDeserializer.deserialize(data, offset); // sublength might be useful
            offset += sublength + 4;
        }
        return new ByteSerializableArray(array);
    }

    private static class ByteSerializableArrayFactory implements ByteSerializableFactory<ByteSerializableArray> {
        @Override
        public ByteSerializableArray deserialize(byte[] data, int offset) {
            return ByteSerializableArray.deserialize(data, offset);
        }
    }
}
