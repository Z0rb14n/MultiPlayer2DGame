package ui.client;

import game.GameController;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(GameController.GAME_WIDTH,GameController.GAME_HEIGHT + 200);

    private static GameFrame singleton;

    public static GameFrame getInstance() {
        if (singleton == null) singleton = new GameFrame();
        return singleton;
    }

    private GameFrame() {
        super("don't sue");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(SIZE);
        setPreferredSize(SIZE);
        ConnectionPanel cp = new ConnectionPanel();
        add(cp);
        setVisible(true);
    }

    public static void main(String[] args) {
        getInstance();
    }
}
