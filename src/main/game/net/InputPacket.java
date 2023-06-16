package game.net;

import game.GameInput;
import net.ByteSerializable;
import net.ByteSerializableFactory;
import net.MagicConstDeserializer;

import java.util.ArrayList;
import java.util.HashMap;

public class InputPacket implements ByteSerializable {
    private static final int MAGIC_NUMBER = 0x42752193;
    private final GameInput[] input;
    /**
     * If false, the key was released
     */
    private final boolean[] pressed;
    static {
        ensureFactoryRegistered();
    }

    public static void ensureFactoryRegistered() {
        MagicConstDeserializer.registerFactory(MAGIC_NUMBER, new InputPacketFactory());
    }

    public static final InputPacket EMPTY = new InputPacket(new GameInput[0], new boolean[0]);
    public static final InputPacket NONE = new InputPacket(new GameInput[]{GameInput.NONE}, new boolean[]{false});

    public InputPacket(ArrayList<GameInput> inputs, ArrayList<Boolean> pressed) {
        if (inputs.size() != pressed.size()) {
            throw new IllegalArgumentException("Input and pressed lists must be the same length");
        }
        this.input = new GameInput[inputs.size()];
        this.pressed = new boolean[pressed.size()];
        for (int i = 0; i < inputs.size(); i++) {
            this.input[i] = inputs.get(i);
            this.pressed[i] = pressed.get(i);
        }
    }

    public InputPacket(GameInput[] input, boolean[] pressed) {
        this.input = input;
        this.pressed = pressed;
        if (input.length != pressed.length) {
            throw new IllegalArgumentException("Input and pressed arrays must be the same length");
        }
    }

    public GameInput[] getInput() {
        return input;
    }

    public boolean[] getPressed() {
        return pressed;
    }

    public HashMap<GameInput, Boolean> getDict() {
        HashMap<GameInput, Boolean> dict = new HashMap<>();
        for (int i = 0; i < input.length; i++) {
            dict.put(input[i], pressed[i]);
        }
        return dict;
    }

    /**
     * Add packets, assuming you receive p1 and then p2 (in that order).
     * <ul>
     *     <li>If a key is pressed in both packets, it is pressed in the final packet.</li>
     *     <li>If a key is pressed in the second packet, it is pressed in the final packet.</li>
     *     <li>If a key is released in the second packet, it is released in the final packet.</li>
     * </ul>
     * @param p1 The first packet
     * @param p2 The second packet
     * @return The sum of the two packets
     */
    public static InputPacket add(InputPacket p1, InputPacket p2) {
        if (p1 == null && p2 == null) return null;
        if (p1 == null) return p2;
        if (p2 == null) return p1;
        ArrayList<GameInput> finalInputs = new ArrayList<>();
        ArrayList<Boolean> finalPressed = new ArrayList<>();
        HashMap<GameInput, Boolean> dict1 = p1.getDict();
        HashMap<GameInput, Boolean> dict2 = p2.getDict();
        for (GameInput input : GameInput.values()) {
            if (dict1.containsKey(input) && dict2.containsKey(input)) {
                finalInputs.add(input);
                if (!dict2.get(input)) finalPressed.add(false);
                else finalPressed.add(true);
            } else if (dict1.containsKey(input)) {
                finalInputs.add(input);
                finalPressed.add(dict1.get(input));
            } else if (dict2.containsKey(input)) {
                finalInputs.add(input);
                finalPressed.add(dict2.get(input));
            }
        }
        GameInput[] finalInputArr = new GameInput[finalInputs.size()];
        boolean[] finalPressedArr = new boolean[finalPressed.size()];
        for (int i = 0; i < finalInputs.size(); i++) {
            finalInputArr[i] = finalInputs.get(i);
            finalPressedArr[i] = finalPressed.get(i);
        }
        return new InputPacket(finalInputArr, finalPressedArr);
    }

    @Override
    public int getMagicNumber() {
        return MAGIC_NUMBER;
    }

    @Override
    public byte[] toByteArray() {
        int packetLen = 1 + 2*input.length;
        byte[] bytes = new byte[packetLen];
        bytes[0] = (byte) input.length;
        for (int i = 0; i < input.length; i++) {
            bytes[1+2*i] = (byte) input[i].ordinal();
            bytes[1+2*i+1] = (byte) (pressed[i] ? 1 : 0);
        }
        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            sb.append(input[i].name());
            sb.append(": ");
            sb.append(pressed[i]);
            sb.append(',');
        }
        return sb.toString();
    }

    private static class InputPacketFactory implements ByteSerializableFactory<InputPacket> {
        @Override
        public InputPacket deserialize(byte[] data, int offset, int len) {
            if (len < 1) return null;
            int inputLen = data[offset];
            if (len < 1 + 2*inputLen) return null;
            GameInput[] input = new GameInput[inputLen];
            boolean[] pressed = new boolean[inputLen];
            for (int i = 0; i < inputLen; i++) {
                input[i] = GameInput.values()[data[offset+1+2*i]];
                pressed[i] = data[offset+1+2*i+1] != 0;
            }
            return new InputPacket(input, pressed);
        }
    }
}
