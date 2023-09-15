package game;

import engine.BoxRenderer;
import engine.GameObject;
import physics.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.HashSet;

class GameTestUIFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(GameController.GAME_WIDTH,GameController.GAME_HEIGHT + 200);
    private static GameTestUIFrame singleton;
    private final MainPanel mp;
    public static GameTestUIFrame getInstance() {
        if (singleton == null) singleton = new GameTestUIFrame();
        return singleton;
    }
    private GameTestUIFrame() {
        super("don't sue");
        Timer timer = new Timer(Math.floorDiv(1000,165), ae -> this.update());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(SIZE);
        setPreferredSize(SIZE);
        mp = new MainPanel();
        add(mp);
        addKeyListener(mp);
        setVisible(true);
        timer.start();
    }

    private void update() {
        mp.update();
        repaint();
    }

    public static void main(String[] args) {
        getInstance();
    }

    static class MainPanel extends JPanel implements KeyListener {
        private final GameController controller = GameController.getInstance();
        private final VehicleObject player;
        MainPanel() {
            super();
            setBackground(Color.WHITE);
            player = controller.addVehicle(new Vec2D(100,100), 0);

            for (GameObject gameObject : GameController.getInstance().getBoundingBoxes()) {
                BoxRenderer renderer = new BoxRenderer(gameObject, Color.BLUE, true);
                gameObject.addBehaviour(renderer);
            }
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
                    controller.createBall(player, 0);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            GameControllerRenderer.render((Graphics2D) g);
        }

        private void handleInputs() {
            float forceAmount = 0.2f;
            float force = 0;

            if (pressedKeyCodes.contains(KeyEvent.VK_W)) {
                force -= forceAmount;
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_A)) {
                //force = force.add(new Vec2D(-forceAmount,0));
                player.rotate(-0.03f);
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_S)) {
                force += forceAmount;
            }
            if (pressedKeyCodes.contains(KeyEvent.VK_D)) {
                //force = force.add(new Vec2D(forceAmount,0));
                player.rotate(0.03f);
            }
            player.accelerate(force);
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
}
