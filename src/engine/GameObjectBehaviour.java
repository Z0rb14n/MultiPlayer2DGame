package engine;

import physics.Vec2D;

public interface GameObjectBehaviour {
    default void update() {}
    default void physicsUpdate() {}

    default void onDestroy() {}

    default void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {}
}
