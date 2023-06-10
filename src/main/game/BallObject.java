package game;

import engine.CircleRenderer;
import engine.GameObject;
import engine.PhysicsBehaviour;
import engine.SceneHierarchyNode;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.Circle;

import java.awt.*;

public class BallObject extends GameObject {
    public static int BALL_COLLISION_MASK = 0b01;
    public static int BALL_ANTI_COLLISION_MASK = 0b10;
    public BallObject(PhysicsEngine engine, SceneHierarchyNode node, Vec2D position, Vec2D velocity) {
        super(position);
        Circle circle = new Circle(Vec2D.ZERO,2);
        PhysicsBehaviour behaviour = new PhysicsBehaviour(this, engine, circle, false);
        behaviour.setCollisionMask(BALL_COLLISION_MASK);
        behaviour.setAntiCollisionMask(BALL_ANTI_COLLISION_MASK);
        behaviour.setVelocity(velocity);
        addBehaviour(behaviour);
        BallBehaviour ballBehaviour = new BallBehaviour(this, node);
        addBehaviour(ballBehaviour);
        CircleRenderer renderer = new CircleRenderer(this, Color.MAGENTA, false);
        addBehaviour(renderer);
    }
}
