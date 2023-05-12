package game;

import physics.CollisionListener;
import physics.PhysicsObject;
import physics.Vec2D;
import physics.shape.Circle;

import java.awt.*;

public class Ball implements CollisionListener {
    public static int BALL_COLLISION_MASK = 0b01;
    private int numCollisions = 0;
    private final PhysicsObject circle;

    public Ball(Vec2D start) {
        circle = new PhysicsObject(new Circle(Vec2D.ZERO,2), start, false);
        circle.setCoefOfRestitution(1);
        circle.addListener(this);
        circle.setCollisionMask(BALL_COLLISION_MASK);
    }

    public PhysicsObject getPhysicsObject() {
        return circle;
    }

    @Override
    public void onCollision(PhysicsObject listenerTarget, PhysicsObject collider, Vec2D mtv) {
        numCollisions++;
        //System.out.println("Circle collision");
        if (numCollisions > 10) {
            GameController.getInstance().removeBall(this);
        }
    }

    protected void finalize() {
        System.out.println("Ball finalized");
    }

    public void render(Graphics2D g) {
        Vec2D center = ((Circle)circle.getTranslatedShape()).getCenter();
        float radius = ((Circle)circle.getTranslatedShape()).getRadius();
        g.setColor(Color.GREEN);
        g.drawOval((int)(center.getX() - radius), (int)(center.getY() - radius), (int)(radius * 2), (int)(radius * 2));
    }
}
