package physics.vis;

import physics.broad.QuadTree;
import physics.broad.QuadTreeNode;

import java.awt.*;

public class QuadTreeRender {
    public static void drawTree(Graphics2D g, QuadTree<?> tree) {
        g.setColor(Color.BLACK);
        g.drawRect((int)tree.getRoot().getBottomLeft().getX(), (int)tree.getRoot().getBottomLeft().getY(), (int)tree.getRoot().getSize().getX(), (int)tree.getRoot().getSize().getY());
        if (tree.getRoot().getChildren() != null) {
            for (QuadTreeNode<?> child : tree.getRoot().getChildren()) {
                drawTree(g, child);
            }
        }
    }
    public static void drawTree(Graphics2D g, QuadTreeNode<?> tree) {
        g.setColor(Color.BLACK);
        g.drawRect((int)tree.getBottomLeft().getX(), (int)tree.getBottomLeft().getY(), (int)tree.getSize().getX(), (int)tree.getSize().getY());
        if (tree.getChildren() != null) {
            for (QuadTreeNode<?> child : tree.getChildren()) {
                drawTree(g, child);
            }
        }
    }
}
