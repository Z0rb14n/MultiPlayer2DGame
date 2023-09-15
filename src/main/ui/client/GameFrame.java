package ui.client;

import game.net.GameStatePacket;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(800,600);

    private static GameFrame singleton;

    public static GameFrame getInstance() {
        if (singleton == null) singleton = new GameFrame();
        return singleton;
    }

    private final ConnectionPanel cp;
    private final GamePanel gp;

    private GameFrame() {
        super("don't sue");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(SIZE);
        setPreferredSize(SIZE);
        cp = new ConnectionPanel();
        gp = new GamePanel();
        add(cp);
        setVisible(true);
    }

    public void startGame() {
        remove(cp);
        add(gp);
        gp.requestFocus();
        gp.signalGameStart();
        repaint();
    }

    public void endGame() {
        remove(gp);
        add(cp);
        repaint();
    }

    public void updatePacket(GameStatePacket packet) {
        gp.update(packet);
    }

    public static void main(String[] args) {
        getInstance();
    }
}
