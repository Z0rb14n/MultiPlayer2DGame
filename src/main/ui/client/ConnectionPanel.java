package ui.client;

import game.GameClientController;
import game.GameLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * First panel seen (i.e. to connect to the server)
 */
class ConnectionPanel extends JPanel {
    private final static String CONNECTION_TIMEOUT = "Timed out.";
    private final static String DEFAULT_COULD_NOT_CONNECT = "Could not connect.";
    private static final Font font = new Font("Arial", Font.PLAIN, 24);
    private final IPEnterBox ipBox;
    private final JButton ipEnterButton = new JButton("Connect");
    private final JLabel errorDisplay = new JLabel("");

    /**
     * Display connection panel: ip enter box + button
     */
    ConnectionPanel() {
        super();
        setBorder(new EmptyBorder(30, 0, 0, 0));
        ipBox = new IPEnterBox();
        ipEnterButton.setFont(font);
        ipEnterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipEnterButton.addActionListener(e -> {
            System.out.print(ipBox.getText());
            attemptLoadClient(ipBox.getText());
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel jl = new JLabel("Enter IP");
        jl.setAlignmentX(Component.CENTER_ALIGNMENT);
        jl.setFont(font);
        add(jl);
        add(ipBox);
        add(ipEnterButton);
        errorDisplay.setForeground(Color.RED);
        errorDisplay.setFont(font);
        errorDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(errorDisplay);
    }

    void updateErrorDisplayed(String error) {
        errorDisplay.setText(error);
        repaint();
    }

    /**
     * Attempts to load the client with given IP
     *
     * @param ip ip of client
     */
    public void attemptLoadClient(String ip) {
        updateErrorDisplayed("");
        GameClientController.NetworkInstantiationResult result = GameClientController.getInstance().connect(ip);
        switch (result) {
            case SUCCESS:
                GameLogger.getDefault().log("Successful connection. Player num: " + GameClientController.getInstance().getPlayerNumber());
                updateErrorDisplayed("");
                setVisible(false);
                // TODO TELL MAIN FRAME TO SWITCH TO GAME PANEL
                break;
            case TIMEOUT:
                GameLogger.getDefault().log("Connection timed out.");
                updateErrorDisplayed(CONNECTION_TIMEOUT);
                break;
            default:
                updateErrorDisplayed(DEFAULT_COULD_NOT_CONNECT);
                GameLogger.getDefault().log("Could not connect.");
                break;
        }
    }

    String getErrorDisplayed() {
        return errorDisplay.getText();
    }

    private class IPEnterBox extends JTextField {
        IPEnterBox() {
            super(20);
            setFont(font);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            addActionListener(e -> {
                // runs when they press the enter key
                if (ipBox.getText().length() != 0) {
                    attemptLoadClient(ipBox.getText());
                }
            }); //when they press the enter key
            getDocument().addDocumentListener(new whenInputChanges());
            setMaximumSize(getPreferredSize());
        }

        private class whenInputChanges implements DocumentListener {

            @Override
            public void insertUpdate(DocumentEvent e) {
                ipEnterButton.setEnabled(!ipBox.getText().isEmpty());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ipEnterButton.setEnabled(!ipBox.getText().isEmpty());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                ipEnterButton.setEnabled(!ipBox.getText().isEmpty());
            }
        }
    }
}
