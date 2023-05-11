package ui;

import util.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

class MainPanel extends JPanel implements KeyListener {
    private GameController controller = GameController.getInstance();
    MainPanel() {
        super();
        setBackground(Color.WHITE);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(new JLabel("Ripoff Tron Game Thing"));
    }

    void update() {
        controller.update();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        controller.render((Graphics2D) g);
    }


    private HashSet<Integer> keyCodes = new HashSet<>(15);

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean isFirstPressed = !keyCodes.contains(e.getKeyCode());
        int keyCode = e.getKeyCode();
        keyCodes.add(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keyCodes.remove(keyCode);
    }
}
