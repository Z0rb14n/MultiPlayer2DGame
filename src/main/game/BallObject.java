package game;

import engine.*;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.Circle;

import java.awt.*;

public class BallObject extends GameObject implements GameObjectBehaviour {
    public static int BALL_COLLISION_MASK = 0b01;
    public static int BALL_ANTI_COLLISION_MASK = 0b10;
    public static int BALL_MAX_BOUNCES = 5;
    private final int id;
    private final SceneHierarchyNode parent;
    private int counter;
    public BallObject(PhysicsEngine engine, SceneHierarchyNode node, Vec2D position, Vec2D velocity, int id) {
        this(engine, node, position, velocity, id, 0);
    }

    public BallObject(PhysicsEngine engine, SceneHierarchyNode node, Vec2D position, Vec2D velocity, int id, int bounceCount) {
        super(position);
        Circle circle = new Circle(Vec2D.ZERO,2);
        PhysicsBehaviour behaviour = new PhysicsBehaviour(this, engine, circle, false);
        behaviour.setCollisionMask(BALL_COLLISION_MASK);
        behaviour.setAntiCollisionMask(BALL_ANTI_COLLISION_MASK);
        behaviour.setVelocity(velocity);
        addBehaviour(behaviour);
        this.parent = node;
        addBehaviour(this);
        if (GlobalRenderToggle.enableRenderer) {
            CircleRenderer renderer = new CircleRenderer(this, Color.MAGENTA, false);
            addBehaviour(renderer);
        }
        this.id = id;
        this.counter = bounceCount;
    }

    public int getId() {
        return id;
    }

    public int getBounceCount() {
        return counter;
    }

    public void destroy() {
        parent.removeObject(this);
        GameController.getInstance().removeBall(this);
    }


    @Override
    public void physicsUpdate() {
        Vec2D pos = getPosition();
        if (pos.getX() < 10 || pos.getX() > GameController.GAME_WIDTH-10 || pos.getY() < 10 || pos.getY() > GameController.GAME_HEIGHT-10) {
            GameLogger.getDefault().log("BALL OUT OF BOUNDS: " + pos, GameLogger.Level.IGNORE);
        }
    }

    @Override
    public void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        counter++;
        //GameLogger.getDefault().log("BOUNCE COUNT " + counter,"DEBUG");
        if (counter > BALL_MAX_BOUNCES) {
            destroy();
        }
    }
}
