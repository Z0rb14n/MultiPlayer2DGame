package physics.vis;

import engine.GameObject;
import physics.PhysicsEngine;
import engine.PhysicsBehaviour;
import physics.Vec2D;
import physics.broad.SpatialGrid;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.Circle;
import physics.shape.RotatedRectangle;
import physics.shape.Triangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

public class EngineTestUIFrame extends SimpleTestFrame {
    private EngineTestUIFrame() {
        super();
        EngineTestUIPanel e = new EngineTestUIPanel();
        add(e);
        addKeyListener(e);
        addMouseMotionListener(e);
        Timer timer = new Timer(1000/165, e);
        timer.start();
        setVisible(true);
    }

    public static void main(String[] args) {
        new EngineTestUIFrame();
    }

    private static class EngineTestUIPanel extends JPanel implements KeyListener, MouseMotionListener, ActionListener {
        private final ArrayList<AxisAlignedBoundingBox> boxes = new ArrayList<>();
        private final ArrayList<Circle> circles = new ArrayList<>();
        //private final PhysicsEngine engine = new PhysicsEngine(new Vec2D(800,600));
        private final PhysicsEngine engine = new PhysicsEngine(new SpatialGrid<>(new Vec2D(100,100)));
        private final PhysicsBehaviour triangle;
        private final RotatedRectangle rotatedRectangle;
        private Vec2D prevMTV = Vec2D.ZERO;
        public EngineTestUIPanel() {
            super();
            for (int i = 0; i < 10; i++) {
                // randomly initialize rectangles
                Vec2D bLeft = new Vec2D((float) (Math.random() * 400)+100, (float)Math.random() * 300);
                Vec2D size = new Vec2D(30,30);
                AxisAlignedBoundingBox box = new AxisAlignedBoundingBox(bLeft, bLeft.add(size));
                PhysicsBehaviour object = new PhysicsBehaviour(new GameObject(), engine, box, true);
                engine.add(object);
                boxes.add(box);
            }
            for (int i = 0; i < 10; i++) {
                // randomly initialize circles
                Vec2D center = new Vec2D((float) (Math.random() * 400)+100, (float)Math.random() * 300);
                Circle circle = new Circle(center, 15);
                PhysicsBehaviour object = new PhysicsBehaviour(new GameObject(), engine, circle, true);
                engine.add(object);
                circles.add(circle);
            }

            triangle = new PhysicsBehaviour(new GameObject(new Vec2D(400,400)), engine, new Triangle(new Vec2D(-10,0), new Vec2D(10,0), new Vec2D(0,20)), false);
            engine.add(triangle);

            // create rotated rectangle
            Vec2D bLeft = new Vec2D(400, 400);
            rotatedRectangle = new RotatedRectangle(bLeft, 45, 45);
            PhysicsBehaviour object = new PhysicsBehaviour(new GameObject(), engine, rotatedRectangle, true);
            rotatedRectangle.rotate(45);
            engine.add(object);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D)g;
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < boxes.size(); i++) {
                AxisAlignedBoundingBox box = boxes.get(i);
                graphics.setColor(Color.BLACK);
                graphics.fillRect((int)box.getBottomLeft().getX(), (int)box.getBottomLeft().getY(), (int)box.getSize().getX(), (int)box.getSize().getY());
            }

            for (int i = 0; i < circles.size(); i++) {
                Circle circle = circles.get(i);
                graphics.setColor(Color.BLACK);
                graphics.fillOval((int)(circle.getCenter().getX() - circle.getRadius()), (int)(circle.getCenter().getY() - circle.getRadius()), (int)circle.getRadius() * 2, (int)circle.getRadius() * 2);
            }

            graphics.setColor(Color.BLACK);
            Triangle transTriangle = (Triangle) triangle.getTranslatedShape();
            fillTriangle(transTriangle, graphics);

            // render triangle's bounding box
            AxisAlignedBoundingBox triangleBox = transTriangle.getAABB();
            graphics.setColor(Color.BLUE);
            graphics.drawRect((int)triangleBox.getBottomLeft().getX(), (int)triangleBox.getBottomLeft().getY(), (int)triangleBox.getSize().getX(), (int)triangleBox.getSize().getY());


            graphics.setColor(Color.RED);
            // TODO SAVE MTV AFTER COLLISION
            Vec2D vertexOne = transTriangle.getVertices()[0];
            Vec2D vertexTwo = vertexOne.add(prevMTV.mult(10));
            graphics.setStroke(new BasicStroke(3));
            graphics.drawLine((int)vertexOne.getX(), (int)vertexOne.getY(), (int)vertexTwo.getX(), (int)vertexTwo.getY());

            // render rotated rectangle
            rotatedRectangle.rotate(0.01f);
            graphics.setColor(Color.BLACK);
            Vec2D[] vertices = rotatedRectangle.getVertices();
            int[] xPoints = new int[vertices.length];
            int[] yPoints = new int[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                xPoints[i] = (int)vertices[i].getX();
                yPoints[i] = (int)vertices[i].getY();
            }
            g.fillPolygon(xPoints, yPoints, vertices.length);

            engine.getBroadphaseStructure().render(graphics, Color.BLACK);
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

        private final HashSet<Integer> pressedKeys = new HashSet<>();

        @Override
        public void keyPressed(KeyEvent e) {
            pressedKeys.add(e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (triangle.getCoefOfRestitution() == 0) {
                    triangle.setCoefOfRestitution(1);
                } else {
                    triangle.setCoefOfRestitution(0);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            pressedKeys.remove(e.getKeyCode());
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            float forceStrength = 0.1f;
            Vec2D force = new Vec2D(0,0);
            if (pressedKeys.contains(KeyEvent.VK_W)) {
                force = force.add(new Vec2D(0,-forceStrength));
            }
            if (pressedKeys.contains(KeyEvent.VK_A)) {
                force = force.add(new Vec2D(-forceStrength,0));
            }
            if (pressedKeys.contains(KeyEvent.VK_S)) {
                force = force.add(new Vec2D(0,forceStrength));
            }
            if (pressedKeys.contains(KeyEvent.VK_D)) {
                force = force.add(new Vec2D(forceStrength,0));
            }
            if (Vec2D.ZERO.equals(force)) {
                if (triangle.getVelocity().sqMag() < 0.1) {
                    triangle.setVelocity(Vec2D.ZERO);
                } else {
                    triangle.setVelocity(triangle.getVelocity().mult(0.9f));
                }
            } else {
                triangle.setVelocity(triangle.getVelocity().add(force.mult(80)));
            }
            long start = System.nanoTime();
            engine.update(1/60f);
            long physicsUpdate = System.nanoTime();
            System.out.println("Physics update took " + (physicsUpdate-start)/1000000f + "ms");
            repaint();
            long end = System.nanoTime();
            System.out.println("Repaint took " + (end-physicsUpdate)/1000000f + "ms");
        }
    }
}