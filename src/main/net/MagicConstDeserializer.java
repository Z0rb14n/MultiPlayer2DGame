package net;

import java.util.HashMap;

public class MagicConstDeserializer {
    private static final HashMap<Integer, ByteSerializableFactory<?>> factories = new HashMap<>();
    public static void registerFactory(int magic, ByteSerializableFactory<?> factory) {
        factories.put(magic, factory);
    }
    public static ByteSerializable deserialize(byte[] data, int offset, int len) {
        if (len < 4) return null;
        int magic = ByteSerializable.readInt(offset, data);
        ByteSerializableFactory<?> factory = factories.get(magic);
        if (factory != null) {
            return factory.deserialize(data, offset + 4, len);
        }
        return null;
    }
}
