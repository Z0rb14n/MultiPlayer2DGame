package net;

// java is f--king stupid and needs a factory instead of static interface
// like wtf?
public interface ByteSerializableFactory<T extends ByteSerializable> {
    default T deserialize(byte[] data) {
        return deserialize(data, 0);
    }
    T deserialize(byte[] data, int startIndex);
}
