package ui.client;

import game.*;
import game.net.GameStatePacket;
import game.net.InputPacket;
import game.net.RespawnRequestPacket;
import util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class GamePanel extends JPanel implements KeyListener {
    private final GameController controller = GameController.getInstance();
    private final GameClientController clientController = GameClientController.getInstance();
    private final Timer timer = new Timer(Math.floorDiv(1000, 165), ae -> this.update());
    private final Object lock = new Object[0];
    private final ArrayDeque<GameStatePacket> packetQueue = new ArrayDeque<>();
    public GamePanel() {
        super();
        setBackground(Color.WHITE);
        addKeyListener(this);
    }

    void signalGameStart() {
        timer.start();
    }

    void update() {
        handleInputs();
        long start = System.nanoTime();

        synchronized (lock) {
            while (!packetQueue.isEmpty()) {
                controller.updateFromPacket(packetQueue.poll());
            }
        }
        long physUpdate = System.nanoTime();
        GameLogger.getDefault().log("From Packet update took " + (physUpdate - start) / 1000000f + "ms", GameLogger.Category.PERFORMANCE);
        repaint();
        long render = System.nanoTime();
        GameLogger.getDefault().log("Render took " + (render - physUpdate) / 1000000f + "ms", GameLogger.Category.PERFORMANCE);
    }

    private long lastUpdate = 0;

    void update(GameStatePacket packet) {
        synchronized (lock) {
            if (packet != null) {
                packetQueue.add(packet);
                long diff = System.nanoTime() - lastUpdate;
                GameLogger.getDefault().log("Packet diff: " + diff / 1000000f + "ms", GameLogger.Category.PERFORMANCE);
                lastUpdate = System.nanoTime();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameControllerRenderer.render((Graphics2D) g, GameClientController.getInstance().getPlayerNumber());
    }

    private void handleInputs() {
        boolean isEmpty = queuedActions.isEmpty();
        if (hasRespawnQueued) {
            GameLogger.getDefault().log("Sending respawn", GameLogger.Category.GAME);
            GameClientController.getInstance().sendPacket(new RespawnRequestPacket());
        }
        hasRespawnQueued = false;
        if (isEmpty) {
            GameClientController.getInstance().sendPacket(InputPacket.EMPTY);
        } else {
            ArrayList<GameInput> inputs = new ArrayList<>();
            ArrayList<Boolean> isAdd = new ArrayList<>();
            while (!queuedActions.isEmpty()) {
                Pair<GameInput, Boolean> action = queuedActions.poll();
                if (action == null) continue;
                inputs.add(action.first);
                isAdd.add(action.second);
            }
            GameClientController.getInstance().sendPacket(new InputPacket(inputs, isAdd));
        }
        if (packetQueue.isEmpty()) {
            VehicleObject vehicleObject = clientController.getVehicleObject();
            if (vehicleObject == null) return;
            float forceAmount = 0.2f;
            float force = 0;

            if (pressedKeyCodes.contains(KeyEvent.VK_W)) {
                force -= forceAmount;
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_A)) {
                //force = force.add(new Vec2D(-forceAmount,0));
                clientController.getVehicleObject().rotate(-0.03f);
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
                force += forceAmount;
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_D)) {
                //force = force.add(new Vec2D(forceAmount,0));
                clientController.getVehicleObject().rotate(0.03f);
            }
            clientController.getVehicleObject().accelerate(force);
        }
    }

    private final HashSet<Integer> pressedKeyCodes = new HashSet<>(15);
    private final ArrayDeque<Pair<GameInput, Boolean>> queuedActions = new ArrayDeque<>();
    private boolean hasRespawnQueued = false;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean isFirstPressed = !pressedKeyCodes.contains(keyCode);
        if (isFirstPressed && keyCode == KeyEvent.VK_SPACE) {
            queuedActions.add(new Pair<>(GameInput.SHOOT, true));
        }
        // WASD
        if (isFirstPressed && keyCode == KeyEvent.VK_W) {
            queuedActions.add(new Pair<>(GameInput.FORWARD, true));
        }
        if (isFirstPressed && keyCode == KeyEvent.VK_A) {
            queuedActions.add(new Pair<>(GameInput.LEFT, true));
        }
        if (isFirstPressed && keyCode == KeyEvent.VK_S) {
            queuedActions.add(new Pair<>(GameInput.BACKWARD, true));
        }
        if (isFirstPressed && keyCode == KeyEvent.VK_D) {
            queuedActions.add(new Pair<>(GameInput.RIGHT, true));
        }

        if (isFirstPressed && keyCode == KeyEvent.VK_R) {
            hasRespawnQueued = true;
        }
        pressedKeyCodes.add(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeyCodes.remove(keyCode);
        // WASD; ignore shoot/reload as it is ignored
        if (keyCode == KeyEvent.VK_W) {
            queuedActions.add(new Pair<>(GameInput.FORWARD, false));
        }
        if (keyCode == KeyEvent.VK_A) {
            queuedActions.add(new Pair<>(GameInput.LEFT, false));
        }
        if (keyCode == KeyEvent.VK_S) {
            queuedActions.add(new Pair<>(GameInput.BACKWARD, false));
        }
        if (keyCode == KeyEvent.VK_D) {
            queuedActions.add(new Pair<>(GameInput.RIGHT, false));
        }
    }
}
