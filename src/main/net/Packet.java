package net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public interface Packet {
    /**
     * Writes object out to out.
     * @param out Output
     * @throws IOException if I/O errors occur while writing to the underlying OutputStream
     */
     void writeObject(ObjectOutputStream out) throws IOException;
    /**
     * Reads object in from in.
     * @param in Input
     * @throws IOException if I/O errors occur while writing to the
     *  underlying OutputStream
     * @throws ClassNotFoundException if the class of a serialized object
     *  could not be found.
     */
    void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

    default byte[] toByteArray() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)){
            oos.writeObject(this);
            oos.flush();
            int objSize = bos.size();
            byte[] array = new byte[objSize + 4];
            System.arraycopy(ByteBuffer.allocate(4).putInt(objSize).array(),0,array,0,4);
            System.arraycopy(bos.toByteArray(),0,array,4,objSize);
            return array;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
