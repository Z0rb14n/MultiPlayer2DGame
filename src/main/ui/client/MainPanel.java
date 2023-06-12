package ui.client;

import game.GameController;
import game.GameLogger;
import engine.PhysicsBehaviour;
import game.VehicleObject;
import physics.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.HashSet;

class MainPanel extends JPanel implements KeyListener {
    private final GameController controller = GameController.getInstance();
    MainPanel() {
        super();
        setBackground(Color.WHITE);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(new JLabel("Game Thing"));
    }

    void update() {
        while (!queuedActions.isEmpty()) {
            handleAction(queuedActions.pop());
        }
        handleInputs();
        long start = System.nanoTime();
        controller.update();
        long physUpdate = System.nanoTime();
        GameLogger.getDefault().log("Physics update took " + (physUpdate - start) / 1000000f + "ms","PERFORMANCE");
        repaint();
        long render = System.nanoTime();
        GameLogger.getDefault().log("Render took " + (render - physUpdate) / 1000000f + "ms","PERFORMANCE");
    }

    private void handleAction(Action pop) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (pop) {
            case CREATE_BALL:
                controller.createBall();
                break;
            default:
                break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        controller.render((Graphics2D) g);
    }

    private void handleInputs() {
        float forceAmount = 0.2f;
        float force = 0;
        VehicleObject object = (VehicleObject) controller.getPlayer();

        if (pressedKeyCodes.contains(KeyEvent.VK_W)) {
            force -= forceAmount;
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_A)) {
            //force = force.add(new Vec2D(-forceAmount,0));
            object.rotate(-0.03f);
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
            force += forceAmount;
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_D)) {
            //force = force.add(new Vec2D(forceAmount,0));
            object.rotate(0.03f);
        }
        object.accelerate(force);
    }


    private final HashSet<Integer> pressedKeyCodes = new HashSet<>(15);
    private final ArrayDeque<Action> queuedActions = new ArrayDeque<>();

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean isFirstPressed = !pressedKeyCodes.contains(e.getKeyCode());
        int keyCode = e.getKeyCode();
        if (isFirstPressed && keyCode == KeyEvent.VK_SPACE) {
            queuedActions.add(Action.CREATE_BALL);
        }
        pressedKeyCodes.add(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeyCodes.remove(keyCode);
    }

    private enum Action {
        CREATE_BALL
    }
}
