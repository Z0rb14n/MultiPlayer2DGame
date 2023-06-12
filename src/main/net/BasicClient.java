package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Processing's Client class but modified to not use PApplet
 */
public class BasicClient implements Runnable {
    private static final int MAX_BUFFER_SIZE = 1 << 27; // 128 MB
    private final ArrayList<NetworkEventReceiver> networkEventReceivers = new ArrayList<>(1);
    private volatile Thread thread;
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private final Object bufferLock = new Object[0];
    private byte[] buffer = new byte[32768];
    private int bufferIndex;
    private int bufferLast;

    public BasicClient(String host, int port, int timeout) throws IOException {
        this.socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), timeout);
        socket.setSoTimeout(timeout);

        input = socket.getInputStream();
        output = socket.getOutputStream();

        thread = new Thread(this);
        thread.start();
    }

    /**
     * @param host address of the server
     * @param port port to read/write from on the server
     */
    public BasicClient(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    /**
     * @param socket any object of type Socket
     */
    public BasicClient(Socket socket) throws IOException {
        this.socket = socket;

        input = socket.getInputStream();
        output = socket.getOutputStream();

        thread = new Thread(this);
        thread.start();
    }

    public void addNetworkEventReceiver(NetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.add(networkEventReceiver);
    }

    public void removeNetworkEventReceiver(NetworkEventReceiver networkEventReceiver) {
        networkEventReceivers.remove(networkEventReceiver);
    }

    public void clearNetworkEventReceivers() {
        networkEventReceivers.clear();
    }

    /**
     * Disconnects from the server and informs listeners. Use to shut the connection when you're
     * finished with the Client.
     */
    public void stop() {
        if (thread != null) {
            for (NetworkEventReceiver networkEventReceiver : networkEventReceivers)
                networkEventReceiver.disconnectEvent(this);
        }
        dispose();
    }


    /**
     * Disconnect from the server and frees resources without informing listeners.
     * <p></p>
     * Generally use {@link BasicClient#stop()} instead.
     */
    public void dispose() {
        thread = null;
        networkEventReceivers.clear();
        try {
            if (input != null) {
                input.close();
                input = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (output != null) {
                output.close();
                output = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        byte[] readBuffer;
        { // make the read buffer same size as socket receive buffer so that
            // we don't waste cycles calling listeners when there is more data waiting
            int readBufferSize = 1 << 16; // 64 KB (default socket receive buffer size)
            try {
                readBufferSize = socket.getReceiveBufferSize();
            } catch (SocketException ignore) {
            }
            readBuffer = new byte[readBufferSize];
        }
        while (Thread.currentThread() == thread) {
            try {
                while (input != null) {
                    int readCount;

                    // try to read a byte using a blocking read.
                    // An exception will occur when the sketch is exits.
                    try {
                        readCount = input.read(readBuffer, 0, readBuffer.length);
                    } catch (SocketException e) {
                        System.err.println("Client SocketException: " + e.getMessage());
                        // the socket had a problem reading so don't try to read from it again.
                        stop();
                        return;
                    }

                    // read returns -1 if end-of-stream occurs (for example if the host disappears)
                    if (readCount == -1) {
                        System.err.println("Client got end-of-stream.");
                        for (NetworkEventReceiver networkEventReceiver : networkEventReceivers)
                            networkEventReceiver.endOfStreamEvent(this);
                        stop();
                        return;
                    }

                    synchronized (bufferLock) {
                        int freeBack = buffer.length - bufferLast;
                        if (readCount > freeBack) {
                            // not enough space at the back
                            int bufferLength = bufferLast - bufferIndex;
                            byte[] targetBuffer = buffer;
                            if (bufferLength + readCount > buffer.length) {
                                // can't fit even after compacting, resize the buffer
                                // find the next power of two which can fit everything in
                                int newSize = Integer.highestOneBit(bufferLength + readCount - 1) << 1;
                                if (newSize > MAX_BUFFER_SIZE) {
                                    // buffer is full because client is not reading (fast enough)
                                    System.err.println("Client: can't receive more data, buffer is full.");
                                    stop();
                                    return;
                                }
                                targetBuffer = new byte[newSize];
                            }
                            // compact the buffer (either in-place or into the new bigger buffer)
                            System.arraycopy(buffer, bufferIndex, targetBuffer, 0, bufferLength);
                            bufferLast -= bufferIndex;
                            bufferIndex = 0;
                            buffer = targetBuffer;
                        }
                        // copy all newly read bytes into the buffer
                        System.arraycopy(readBuffer, 0, buffer, bufferLast, readCount);
                        bufferLast += readCount;
                    }

                    // now post an event
                    for (NetworkEventReceiver networkEventReceiver : networkEventReceivers)
                        networkEventReceiver.dataReceivedEvent(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Returns true if this client is still active and hasn't run
     * into any trouble.
     */
    public boolean active() {
        return thread != null;
    }


    /**
     * Returns the IP address of the computer to which the Client is attached.
     */
    public String ip() {
        if (socket != null) {
            return socket.getInetAddress().getHostAddress();
        }
        return null;
    }


    /**
     * Returns the number of bytes available. When any client has bytes
     * available from the server, it returns the number of bytes.
     */
    public int available() {
        synchronized (bufferLock) {
            return (bufferLast - bufferIndex);
        }
    }


    /**
     * Empty the buffer, removes all the data stored there.
     */
    public void clear() {
        synchronized (bufferLock) {
            bufferLast = 0;
            bufferIndex = 0;
        }
    }


    /**
     * Returns a number between 0 and 255 for the next byte that's waiting in
     * the buffer. Returns -1 if there is no byte, although this should be
     * avoided by first checking {@link BasicClient#available()} to see if any data is available.
     */
    public byte read() {
        synchronized (bufferLock) {
            if (bufferIndex == bufferLast) return -1;

            byte outgoing = (byte) (buffer[bufferIndex++] & 0xff);
            if (bufferIndex == bufferLast) {  // rewind
                bufferIndex = 0;
                bufferLast = 0;
            }
            return outgoing;
        }
    }


    /**
     * Reads a group of bytes from the buffer. The version with no parameters
     * returns a byte array of all data in the buffer. This is not efficient,
     * but is easy to use. The version with the byteBuffer parameter is
     * more memory and time efficient. It grabs the data in the buffer and puts
     * it into the byte array passed in and returns an int value for the number
     * of bytes read. If more bytes are available than can fit into the
     * byteBuffer, only those that fit are read.
     * <p></p>
     * Return a byte array of anything that's in the serial buffer.
     * Not particularly memory/speed efficient, because it creates
     * a byte array on each read, but it's easier to use than
     * {@link net.BasicClient#readBytes(byte[])}.
     */
    public byte[] readBytes() {
        synchronized (bufferLock) {
            if (bufferIndex == bufferLast) return null;

            int length = bufferLast - bufferIndex;
            byte[] outgoing = new byte[length];
            System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

            bufferIndex = 0;  // rewind
            bufferLast = 0;
            return outgoing;
        }
    }


    /**
     * Return a byte array of anything that's in the serial buffer
     * up to the specified maximum number of bytes.
     * Not particularly memory/speed efficient, because it creates
     * a byte array on each read, but it's easier to use than
     * {@link net.BasicClient#readBytes(byte[])}.
     *
     * @param max the maximum number of bytes to read
     */
    public byte[] readBytes(int max) {
        synchronized (bufferLock) {
            if (bufferIndex == bufferLast) return null;

            int length = bufferLast - bufferIndex;
            if (length > max) length = max;
            byte[] outgoing = new byte[length];
            System.arraycopy(buffer, bufferIndex, outgoing, 0, length);

            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }

            return outgoing;
        }
    }


    /**
     * Grab whatever is in the serial buffer, and stuff it into a
     * byte buffer passed in by the user. This is more memory/time
     * efficient than readBytes() returning a byte[] array.
     * <p></p>
     * Returns an int for how many bytes were read. If more bytes
     * are available than can fit into the byte array, only those
     * that will fit are read.
     *
     * @param bytebuffer passed in byte array to be altered
     */
    public int readBytes(byte[] bytebuffer) {
        synchronized (bufferLock) {
            if (bufferIndex == bufferLast) return 0;

            int length = bufferLast - bufferIndex;
            if (length > bytebuffer.length) length = bytebuffer.length;
            System.arraycopy(buffer, bufferIndex, bytebuffer, 0, length);

            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }
            return length;
        }
    }

    /**
     * Returns the all the data from the buffer as a UTF8 String.
     */
    public String readString() {
        byte[] b = readBytes();
        if (b == null) return null;
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(b)).toString();
    }

    /**
     * Writes data to a server specified when constructing the client.
     *
     * @param data data to write
     */
    public void writeByte(byte data) {
        try {
            output.write(data);
            output.flush();   // hmm, not sure if a good idea
        } catch (Exception e) { // null pointer or serial port dead
            e.printStackTrace();
            stop();
        }
    }

    public void writeShort(short int16) {
        writeBytes(ByteBuffer.allocate(2).putShort(int16).array());
    }

    public short readShort() {
        return ByteBuffer.wrap(readBytes(2)).getShort();
    }

    public void writeInt(int int32) {
        writeBytes(ByteBuffer.allocate(4).putInt(int32).array());
    }

    public int readInt() {
        return ByteBuffer.wrap(readBytes(4)).getInt();
    }

    public void writeLong(long int64) {
        writeBytes(ByteBuffer.allocate(8).putLong(int64).array());
    }

    public long readLong() {
        return ByteBuffer.wrap(readBytes(8)).getLong();
    }

    public void writeFloat(float float32) {
        writeBytes(ByteBuffer.allocate(4).putFloat(float32).array());
    }

    public float readFloat() {
        return ByteBuffer.wrap(readBytes(4)).getFloat();
    }

    public void writePacket(ByteSerializable packet) {
        byte[] bytes = packet.toByteArray();
        try {
            output.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
            output.write(ByteBuffer.allocate(4).putInt(packet.getMagicNumber()).array());
            output.write(bytes);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            stop();
        }
    }

    public ByteSerializable readPacket() {
        synchronized (bufferLock) {
            int length = bufferLast - bufferIndex;
            if (length < 8) return null; // 4 for length; 4 for magic const
            int desiredLen = ByteBuffer.wrap(buffer,bufferIndex,4).getInt();
            if (length < desiredLen + 8) return null;

            bufferIndex += desiredLen+8;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }

            return MagicConstDeserializer.deserialize(buffer, bufferIndex+4);
        }
    }

    public void writeBytes(byte[] data) {
        try {
            output.write(data);
            output.flush();   // hmm, not sure if a good idea
        } catch (Exception e) { // null pointer or serial port dead
            e.printStackTrace();
            stop();
        }
    }

    public void writeStr(String data) {
        writeBytes(data.getBytes(StandardCharsets.UTF_8));
    }
}
