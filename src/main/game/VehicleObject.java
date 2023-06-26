package game;

import engine.*;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.RotatedTriangle;

import java.awt.*;

public class VehicleObject extends GameObject implements GameObjectBehaviour {
    public static int VEHICLE_COLLISION_MASK = 0b10;
    private final RotatedTriangle triangle;
    private final PhysicsBehaviour behaviour;
    private static final float maxSpeed = 300;
    private static final Vec2D[] vertices = new Vec2D[3];
    static {
        vertices[0] = new Vec2D(-10,10);
        vertices[1] = new Vec2D(10,10);
        vertices[2] = new Vec2D(0,-10);
    }
    private final int id;
    public VehicleObject(PhysicsEngine engine, Vec2D position, int id) {
        this(engine, position, id, 0);
    }

    public VehicleObject(PhysicsEngine engine, Vec2D position, int id, float angle) {
        super(position);
        triangle = new RotatedTriangle(vertices);
        triangle.setAngle(angle);
        behaviour = new PhysicsBehaviour(this,engine,triangle,false);
        behaviour.setCollisionMask(VEHICLE_COLLISION_MASK);
        addBehaviour(behaviour);
        RotatedTriangleRenderer renderer = new RotatedTriangleRenderer(this, Color.RED, true);
        addBehaviour(renderer);
        addBehaviour(this);
        this.id = id;
    }

    public void rotate(float angle) {
        triangle.rotate(angle);
        behaviour.setShape(triangle); // screws with the shape; reset it
    }

    public void setAngle(float angle) {
        triangle.setAngle(angle);
        behaviour.setShape(triangle); // screws with the shape; reset it
    }

    public void accelerate(float amount) {
        if (amount == 0) {
            if (behaviour.getVelocity().sqMag() < 0.1) {
                behaviour.setVelocity(Vec2D.ZERO);
            } else {
                behaviour.setVelocity(behaviour.getVelocity().mult(0.9f));
            }
        } else {
            Vec2D force = new Vec2D(0,amount);
            force = force.rotated(triangle.getAngle());
            behaviour.setVelocity(behaviour.getVelocity().add(force.mult(80)));
        }

        if (behaviour.getVelocity().sqMag() > maxSpeed * maxSpeed) {
            behaviour.setVelocity(behaviour.getVelocity().scaleTo(maxSpeed));
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        GameObject other = target.getParentObject();
        if (other == this) {
            other = src.getParentObject();
            System.out.println("lol -- VEHICLE OBJECT ON COLLISION FUNNY");
        }

        if (other instanceof BallObject) {
            BallObject ball = (BallObject) other;
            if (ball.getId() == id) {
                return;
            }
            System.out.println("vehicle collided with ball");
            ball.destroy();
        }
    }
}
