package physics;

import physics.shape.ConvexShape;

import java.util.ArrayList;
import java.util.Objects;

public class PhysicsObject {
    private ConvexShape shape;
    private ConvexShape translatedShape;
    private Vec2D position;
    private Vec2D velocity;
    private boolean stationary;
    private boolean awake;
    private float mass;
    private float coefOfRestitution = 1f;
    private int collisionMask = 0xffffffff;

    private final ArrayList<CollisionListener> collisionListeners = new ArrayList<>();

    private final ArrayList<PhysicsUpdateListener> updateListeners = new ArrayList<>();

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
        translatedShape.translate(position);
    }

    @SuppressWarnings("unused")
    public void setStationary(boolean stationary) {
        this.stationary = stationary;
        if (stationary) velocity = Vec2D.ZERO;
        if (stationary) awake = false;
    }

    @SuppressWarnings("unused")
    public void setPosition(Vec2D position) {
        this.position = position;
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

    public void addCollisionListener(CollisionListener listener) {
        collisionListeners.add(listener);
    }

    @SuppressWarnings("unused")
    public void removeCollisionListener(CollisionListener listener) {
        collisionListeners.remove(listener);
    }

    public void addUpdateListener(PhysicsUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void removeUpdateListener(PhysicsUpdateListener listener) {
        updateListeners.remove(listener);
    }

    @SuppressWarnings("unused")
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

    public boolean updated = false;

    public boolean physicsUpdate(float dt) {
        if (awake && !stationary) {
            if (updated) {
                System.err.println("PhysicsObject.physicsUpdate() called twice in one frame");
            }
            updated = true;
            if (Vec2D.ZERO.equals(velocity)) {
                awake = false;
            } else {
                translate(velocity.mult(dt));
                for(PhysicsUpdateListener listener : updateListeners) {
                    listener.onPhysicsUpdate(this, dt);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhysicsObject object = (PhysicsObject) o;
        return stationary == object.stationary && Float.compare(object.mass, mass) == 0 && Float.compare(object.coefOfRestitution, coefOfRestitution) == 0 && collisionMask == object.collisionMask && Objects.equals(shape, object.shape) && Objects.equals(position, object.position) && Objects.equals(velocity, object.velocity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, position, velocity, stationary, mass, coefOfRestitution, collisionMask);
    }
}
