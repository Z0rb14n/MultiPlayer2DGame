package physics.vis;

import javax.swing.*;
import java.awt.*;

public abstract class SimpleTestFrame extends JFrame  {
    protected static final Dimension SIZE = new Dimension(800,600);
    public SimpleTestFrame() {
        super("don't sue");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(SIZE);
        setPreferredSize(SIZE);
    }
}
