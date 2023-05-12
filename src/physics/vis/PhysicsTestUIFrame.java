package physics.vis;

import physics.Vec2D;
import physics.broad.QuadTree;
import physics.broad.QuadTreeEntry;
import physics.broad.QuadTreeNode;
import physics.narrow.SATTest;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.ConvexShape;
import physics.shape.Triangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PhysicsTestUIFrame extends SimpleTestFrame {
    private PhysicsTestUIFrame() {
        super();
        PhysicsTestUIPanel ptup = new PhysicsTestUIPanel();
        add(ptup);
        addKeyListener(ptup);
        addMouseMotionListener(ptup);
        setVisible(true);
    }

    public static void main(String[] args) {
        new PhysicsTestUIFrame();
    }

    private static class PhysicsTestUIPanel extends JPanel implements KeyListener, MouseMotionListener {
        private QuadTree<ConvexShape> tree;

        private ArrayList<AxisAlignedBoundingBox> rectangles = new ArrayList<>();
        private Triangle triangleOne;
        public PhysicsTestUIPanel() {
            super();
            tree = new QuadTree<>(new Vec2D(800, 600), 5, 6);
            AxisAlignedBoundingBox rect = new AxisAlignedBoundingBox(new Vec2D(100,100), new Vec2D(200,200));
            tree.insert(rect, rect);
            rectangles.add(rect);
            rect = new AxisAlignedBoundingBox(new Vec2D(250,250), new Vec2D(350,350));
            tree.insert(rect, rect);
            rectangles.add(rect);
            triangleOne = new Triangle(new Vec2D(400,400), new Vec2D(420,400), new Vec2D(410, 420));
            tree.insert(triangleOne, triangleOne);
            for (int i = 0; i < 10; i++) {
                // randomly initialize rectangles
                Vec2D bLeft = new Vec2D((float) (Math.random() * 400)+100, (float)Math.random() * 300);
                Vec2D size = new Vec2D(10,10);
                AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(bLeft, bLeft.add(size));
                tree.insert(box, box);
                rectangles.add(box);
            }
        }

        private void drawTree(Graphics2D g, QuadTree<?> tree) {
            g.setColor(Color.BLACK);
            g.drawRect((int)tree.getRoot().getBottomLeft().getX(), (int)tree.getRoot().getBottomLeft().getY(), (int)tree.getRoot().getSize().getX(), (int)tree.getRoot().getSize().getY());
            if (tree.getRoot().getChildren() != null) {
                for (QuadTreeNode<?> child : tree.getRoot().getChildren()) {
                    drawTree(g, child);
                }
            }
        }
        private void drawTree(Graphics2D g, QuadTreeNode<?> tree) {
            g.setColor(Color.BLACK);
            g.drawRect((int)tree.getBottomLeft().getX(), (int)tree.getBottomLeft().getY(), (int)tree.getSize().getX(), (int)tree.getSize().getY());
            if (tree.getChildren() != null) {
                for (QuadTreeNode<?> child : tree.getChildren()) {
                    drawTree(g, child);
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D)g;
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < rectangles.size(); i++) {
                AxisAlignedBoundingBox box = rectangles.get(i);
                ArrayList<QuadTreeEntry<ConvexShape>> closeObjects = tree.findCloseObjects(box);
                graphics.setColor(Color.BLACK);
                for (int j = 0; j < closeObjects.size(); j++) {
                    if (closeObjects.get(j).getObject() == box) continue;
                    if (SATTest.hasIntersection(box, closeObjects.get(j).getObject())) {
                        graphics.setColor(Color.RED);
                        break;
                    }
                }
                graphics.fillRect((int)box.getBottomLeft().getX(), (int)box.getBottomLeft().getY(), (int)box.getSize().getX(), (int)box.getSize().getY());
            }

            ArrayList<QuadTreeEntry<ConvexShape>> closeObjects = tree.findCloseObjects(triangleOne);
            graphics.setColor(Color.BLACK);
            for (int j = 0; j < closeObjects.size(); j++) {
                if (closeObjects.get(j).getObject() == triangleOne) continue;
                Vec2D mtv = SATTest.getMTV(triangleOne, closeObjects.get(j).getObject());
                if (mtv != null) {
                    graphics.setColor(Color.GREEN);
                    Vec2D vertexOne = triangleOne.getVertices()[0];
                    Vec2D vertexTwo = vertexOne.add(mtv.mult(10));
                    graphics.setStroke(new BasicStroke(3));
                    graphics.drawLine((int)vertexOne.getX(), (int)vertexOne.getY(), (int)vertexTwo.getX(), (int)vertexTwo.getY());
                    graphics.setColor(Color.RED);
                    break;
                }
            }
            fillTriangle(triangleOne, graphics);
            Vec2D v1 = triangleOne.getVertices()[0];
            Vec2D v2 = triangleOne.getVertices()[1];
            Vec2D v3 = triangleOne.getVertices()[2];
            Vec2D n1 = triangleOne.getPerpAxis()[0];
            Vec2D n2 = triangleOne.getPerpAxis()[1];
            Vec2D n3 = triangleOne.getPerpAxis()[2];
            graphics.setColor(Color.BLUE);
            graphics.setStroke(new BasicStroke(3));
            graphics.drawLine((int)v1.getX(), (int)v1.getY(), (int)(v1.getX()+n1.getX()*10), (int)(v1.getY()+n1.getY()*10));
            graphics.drawLine((int)v2.getX(), (int)v2.getY(), (int)(v2.getX()+n2.getX()*10), (int)(v2.getY()+n2.getY()*10));
            graphics.drawLine((int)v3.getX(), (int)v3.getY(), (int)(v3.getX()+n3.getX()*10), (int)(v3.getY()+n3.getY()*10));


            drawTree(graphics, tree);
        }

        private void fillTriangle(Triangle triangle, Graphics2D graphics) {
            Vec2D[] vertices = triangle.getVertices();
            int[] xPoints = new int[vertices.length];
            int[] yPoints = new int[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                xPoints[i] = (int)vertices[i].getX();
                yPoints[i] = (int)vertices[i].getY();
            }
            graphics.fillPolygon(xPoints, yPoints, vertices.length);
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Vec2D mousePos = new Vec2D(e.getX(), e.getY());
            mousePos = mousePos.sub(triangleOne.getVertices()[0]);
            ConvexShape prev = triangleOne.copy();
            triangleOne.translate(mousePos);
            tree.update(triangleOne, prev, triangleOne);
            repaint();
        }
    }
}
