package physics;

import physics.shape.ConvexShape;

import java.util.ArrayList;

public class PhysicsObject {
    private ConvexShape shape;
    private ConvexShape translatedShape;
    private Vec2D position;
    private Vec2D velocity;
    private boolean stationary;
    private boolean awake;
    private float mass = 1;
    private float coefOfRestitution = 1f;

    private ArrayList<CollisionListener> collisionListeners = new ArrayList<>();

    public PhysicsObject(ConvexShape shape) {
        this(shape,Vec2D.ZERO,false);
    }

    public PhysicsObject(ConvexShape shape, Vec2D position) {
        this(shape,position,false);
    }

    public PhysicsObject(ConvexShape shape, Vec2D position, boolean stationary) {
        this.shape = shape.copy();
        this.translatedShape = shape.copy();
        translatedShape.translate(position);
        this.position = position.copy();
        velocity = Vec2D.ZERO;
        this.stationary = stationary;
        this.mass = 1;
    }

    public PhysicsObject(ConvexShape shape, Vec2D position, float mass) {
        this.shape = shape.copy();
        this.translatedShape = shape.copy();
        translatedShape.translate(position);
        this.position = position.copy();
        velocity = Vec2D.ZERO;
        this.stationary = false;
        this.mass = mass;
    }

    public ConvexShape getShape() {
        return shape;
    }

    public ConvexShape getTranslatedShape() {
        return translatedShape;
    }

    public Vec2D getPosition() {
        return position;
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

    public void setCoefOfRestitution(float coefOfRestitution) {
        this.coefOfRestitution = coefOfRestitution;
    }

    public void setShape(ConvexShape shape) {
        this.shape = shape.copy();
        translatedShape = shape.copy();
        translatedShape.translate(position);
    }

    public void setStationary(boolean stationary) {
        this.stationary = stationary;
        if (stationary) velocity = Vec2D.ZERO;
        if (stationary) awake = false;
    }

    public void setPosition(Vec2D position) {
        this.position = position;
        translatedShape = shape.copy();
        translatedShape.translate(position);
    }

    public void setVelocity(Vec2D velocity) {
        this.velocity = velocity;
        if (!velocity.equals(Vec2D.ZERO)) awake = true;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void addListener(CollisionListener listener) {
        collisionListeners.add(listener);
    }

    public void removeListener(CollisionListener listener) {
        collisionListeners.remove(listener);
    }

    public void clearListeners() {
        collisionListeners.clear();
    }

    public void onCollision(PhysicsObject other, Vec2D mtv) {
        for (CollisionListener listener : collisionListeners) {
            listener.onCollision(this, other, mtv);
        }
    }

    public void translate(Vec2D translation) {
        position = position.add(translation);
        translatedShape.translate(translation);
    }

    public boolean update(float dt) {
        if (awake && !stationary) {
            if (Vec2D.ZERO.equals(velocity)) {
                awake = false;
            } else {
                translate(velocity.mult(dt));
                return true;
            }
        }
        return false;
    }
}
