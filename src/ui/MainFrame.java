package ui;

import util.GameController;

import javax.swing.*;
import java.awt.*;

class MainFrame extends JFrame {
    private static final Dimension SIZE = new Dimension(GameController.GAME_WIDTH,GameController.GAME_HEIGHT + 200);
    private static MainFrame singleton;
    private final MainPanel mp;
    public static MainFrame getInstance() {
        if (singleton == null) singleton = new MainFrame();
        return singleton;
    }
    private MainFrame() {
        super("don't sue");
        Timer timer = new Timer(Math.floorDiv(1000,60), ae -> this.update());
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
        repaint();
        mp.update();
    }

    public static void main(String[] args) {
        getInstance();
    }
}
