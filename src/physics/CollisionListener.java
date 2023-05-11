package physics;

public interface CollisionListener {
    void onCollision(PhysicsObject listenerTarget, PhysicsObject collider, Vec2D mtv);
}
