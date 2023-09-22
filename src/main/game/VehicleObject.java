package game;

import engine.GameObject;
import engine.GameObjectBehaviour;
import engine.PhysicsBehaviour;
import engine.RotatedTriangleRenderer;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.RotatedTriangle;

import java.awt.*;

public class VehicleObject extends GameObject implements GameObjectBehaviour {
    public static int VEHICLE_COLLISION_MASK = 0b11;
    private final RotatedTriangle triangle;
    private final RotatedTriangleRenderer renderer;
    private final PhysicsBehaviour behaviour;
    private static final float maxSpeed = 300;
    private static final Vec2D[] vertices = new Vec2D[3];
    static {
        vertices[0] = new Vec2D(-10,10);
        vertices[1] = new Vec2D(10,10);
        vertices[2] = new Vec2D(0,-10);
    }
    private final int id;
    private boolean dead;
    public VehicleObject(PhysicsEngine engine, Vec2D position, int id) {
        this(engine, position, id, 0, false);
    }

    public VehicleObject(PhysicsEngine engine, Vec2D position, int id, float angle, boolean dead) {
        super(position);
        triangle = new RotatedTriangle(vertices);
        triangle.setAngle(angle);
        behaviour = new PhysicsBehaviour(this,engine,triangle,false);
        behaviour.setCollisionMask(VEHICLE_COLLISION_MASK);
        addBehaviour(behaviour);
        if (GlobalRenderToggle.enableRenderer) {
            renderer = new RotatedTriangleRenderer(this, dead ? Color.MAGENTA : Color.RED, true);
            addBehaviour(renderer);
        } else renderer = null;
        addBehaviour(this);
        this.id = id;
        this.dead = dead;
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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        if (GlobalRenderToggle.enableRenderer) renderer.setColor(dead ? Color.MAGENTA : Color.RED);
    }

    @Override
    public void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        GameObject other = target.getParentObject();
        if (other == this) {
            other = src.getParentObject();
            GameLogger.getDefault().log("Vehicle Object Collision src/target swap", GameLogger.Category.COLLISION);
        }

        if (other instanceof BallObject) {
            BallObject ball = (BallObject) other;
            if (ball.getId() == id) {
                return;
            }
            GameLogger.getDefault().log("Vehicle collided with ball", GameLogger.Category.COLLISION);
            ball.destroy();
            GameController.getInstance().onDeath(this, ball.getId());
        }
    }
}
