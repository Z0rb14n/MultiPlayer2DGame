package ui;

import game.GameController;
import physics.PhysicsObject;
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
        //long start = System.nanoTime();
        controller.update();
        //long physUpdate = System.nanoTime();
        //System.out.println("Physics update took " + (physUpdate - start) / 1000000f + "ms");
        repaint();
        //long render = System.nanoTime();
        //System.out.println("Render took " + (render - physUpdate) / 1000000f + "ms");
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
        float maxSpeed = 400;
        Vec2D force = new Vec2D(0,0);
        if (pressedKeyCodes.contains(KeyEvent.VK_W)) {
            force = force.add(new Vec2D(0,-forceAmount));
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_A)) {
            force = force.add(new Vec2D(-forceAmount,0));
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
            force = force.add(new Vec2D(0,forceAmount));
        }
        if (pressedKeyCodes.contains(KeyEvent.VK_D)) {
            force = force.add(new Vec2D(forceAmount,0));
        }
        PhysicsObject triangle = controller.getPlayer().getPhysicsObject();
        if (Vec2D.ZERO.equals(force)) {
            if (triangle.getVelocity().sqMag() < 0.1) {
                triangle.setVelocity(Vec2D.ZERO);
            } else {
                triangle.setVelocity(triangle.getVelocity().mult(0.9f));
            }
        } else {
            triangle.setVelocity(triangle.getVelocity().add(force.mult(80)));
        }
        if (triangle.getVelocity().sqMag() > maxSpeed * maxSpeed) {
            triangle.setVelocity(triangle.getVelocity().scaleTo(maxSpeed));
        }
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
