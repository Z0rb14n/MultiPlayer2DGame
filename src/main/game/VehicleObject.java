package game;

import engine.*;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.RotatedTriangle;

import java.awt.*;

public class VehicleObject extends GameObject {
    public static int VEHICLE_COLLISION_MASK = 0b10;
    private RotatedTriangle triangle;
    private PhysicsBehaviour behaviour;
    private float maxSpeed = 400;
    public VehicleObject(PhysicsEngine engine, Vec2D position) {
        super(position);
        Vec2D[] vertices = new Vec2D[3];
        vertices[0] = new Vec2D(-10,0);
        vertices[1] = new Vec2D(10,0);
        vertices[2] = new Vec2D(0,-20);
        triangle = new RotatedTriangle(vertices);
        behaviour = new PhysicsBehaviour(this,engine,triangle,false);
        behaviour.setCollisionMask(VEHICLE_COLLISION_MASK);
        addBehaviour(behaviour);
        RotatedTriangleRenderer renderer = new RotatedTriangleRenderer(this, Color.RED, true);
        addBehaviour(renderer);
    }

    public void rotate(float angle) {
        triangle.rotate(angle);
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


}
