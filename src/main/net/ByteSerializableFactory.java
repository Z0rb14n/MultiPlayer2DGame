package net;

// java is f--king stupid and needs a factory instead of static interface
// like wtf?
public interface ByteSerializableFactory<T extends ByteSerializable> {
    default T deserialize(byte[] data) {
        return deserialize(data, 0);
    }

    /**
     * Deserializes the byte array into an object. The byte array should not include magic numbers and byte array length.
     * @param data the byte array
     * @param startIndex the start index of the byte array
     * @return the deserialized object
     */
    T deserialize(byte[] data, int startIndex);
}
