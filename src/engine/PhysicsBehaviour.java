package engine;

import physics.PhysicsEngine;
import physics.Vec2D;
import physics.shape.ConvexShape;

public class PhysicsBehaviour implements GameObjectBehaviour {
    private ConvexShape shape;
    private ConvexShape translatedShape;
    private Vec2D velocity;
    private boolean stationary;
    private boolean awake;
    private float mass;
    private float coefOfRestitution = 1f;
    private int collisionMask = 0xffffffff;
    private final PhysicsEngine engine;
    private final GameObject parent;

    public PhysicsBehaviour(GameObject parent, PhysicsEngine engine, ConvexShape shape) {
        this(parent,engine,shape,false);
    }
    public PhysicsBehaviour(GameObject parent, PhysicsEngine engine, ConvexShape shape, boolean stationary) {
        this.parent = parent;
        this.engine = engine;
        this.shape = shape.copy();
        this.translatedShape = shape.copy();
        translatedShape.translate(parent.getPosition());
        velocity = Vec2D.ZERO;
        this.stationary = stationary;
        this.mass = 1;
        engine.add(this);
    }
    public PhysicsBehaviour(GameObject parent, PhysicsEngine engine, ConvexShape shape, float mass) {
        this.parent = parent;
        this.engine = engine;
        this.shape = shape.copy();
        this.translatedShape = shape.copy();
        translatedShape.translate(parent.getPosition());
        velocity = Vec2D.ZERO;
        this.stationary = false;
        this.mass = mass;
        engine.add(this);
    }

    @Override
    public void onDestroy() {
        engine.remove(this);
    }

    public void removeOOB() {
        engine.removeImmediateOOB(this);
    }

    public ConvexShape getShape() {
        return shape;
    }

    public ConvexShape getTranslatedShape() {
        return translatedShape;
    }

    public Vec2D getPosition() {
        return parent.getPosition();
    }

    public Vec2D getVelocity() {
        return velocity;
    }

    public boolean isStationary() {
        return stationary;
    }

    public boolean isAwake() {
        return awake;
    }

    public float getMass() {
        return mass;
    }

    public float getCoefOfRestitution() {
        return coefOfRestitution;
    }

    public int getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionMask(int collisionMask) {
        this.collisionMask = collisionMask;
    }

    public void setCoefOfRestitution(float coefOfRestitution) {
        this.coefOfRestitution = coefOfRestitution;
    }

    public void setShape(ConvexShape shape) {
        this.shape = shape.copy();
        translatedShape = shape.copy();
        translatedShape.translate(parent.getPosition());
    }

    @SuppressWarnings("unused")
    public void setStationary(boolean stationary) {
        this.stationary = stationary;
        if (stationary) velocity = Vec2D.ZERO;
        if (stationary) awake = false;
    }

    @SuppressWarnings("unused")
    public void setPosition(Vec2D position) {
        parent.setPosition(position);
        translatedShape = shape.copy();
        translatedShape.translate(position);
    }

    @SuppressWarnings("unused")
    public void setVelocity(Vec2D velocity) {
        this.velocity = velocity;
        if (!velocity.equals(Vec2D.ZERO)) awake = true;
    }

    @SuppressWarnings("unused")
    public void setMass(float mass) {
        this.mass = mass;
    }

    public void onCollision(PhysicsBehaviour other, Vec2D mtv) {
        parent.triggerCollision(this, other, mtv);
    }

    public void translate(Vec2D translation) {
        parent.setPosition(parent.getPosition().add(translation));
        translatedShape.translate(translation);
    }

    public ConvexShape physicsUpdate(float dt) {
        if (awake && !stationary) {
            if (Vec2D.ZERO.equals(velocity)) {
                awake = false;
            } else {
                ConvexShape prev = translatedShape.copy();
                translate(velocity.mult(dt));
                return prev;
            }
        }
        return null;
    }
}
