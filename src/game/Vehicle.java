package game;

import physics.CollisionListener;
import physics.PhysicsObject;
import physics.Vec2D;
import physics.shape.Triangle;

import java.awt.*;

public class Vehicle implements CollisionListener {
    public static int VEHICLE_COLLISION_MASK = 0b10;
    private final PhysicsObject triangle;

    public Vehicle(Vec2D start) {
        triangle = new PhysicsObject(new Triangle(new Vec2D(-10,0), new Vec2D(10,0), new Vec2D(0,20)), start, false);
        triangle.addCollisionListener(this);
        triangle.setCollisionMask(VEHICLE_COLLISION_MASK);
    }

    public PhysicsObject getPhysicsObject() {
        return triangle;
    }

    @Override
    public void onCollision(PhysicsObject listenerTarget, PhysicsObject collider, Vec2D mtv) {
        GameLogger.getDefault().log("Vehicle collision","VEHICLE_DEBUG");
    }

    public void render(Graphics2D g) {
        Vec2D[] points = triangle.getTranslatedShape().getVertices();
        g.drawLine((int)points[0].getX(), (int)points[0].getY(), (int)points[1].getX(), (int)points[1].getY());
        g.drawLine((int)points[1].getX(), (int)points[1].getY(), (int)points[2].getX(), (int)points[2].getY());
        g.drawLine((int)points[2].getX(), (int)points[2].getY(), (int)points[0].getX(), (int)points[0].getY());
    }
}
