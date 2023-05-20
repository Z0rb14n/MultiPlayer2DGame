package engine;

import physics.Vec2D;

import java.util.ArrayList;

public class GameObject {
    private Vec2D position;
    private final ArrayList<GameObjectBehaviour> behaviours = new ArrayList<>();
    public GameObject() {
        this(Vec2D.ZERO);
    }

    public GameObject(Vec2D position) {
        this.position = position;
    }

    public Vec2D getPosition() {
        return position;
    }

    public void setPosition(Vec2D position) {
        this.position = position;
    }

    public void addBehaviour(GameObjectBehaviour behaviour) {
        behaviours.add(behaviour);
    }

    public void removeBehaviour(GameObjectBehaviour behaviour) {
        behaviours.remove(behaviour);
    }

    public void update() {
        for(GameObjectBehaviour behaviour : behaviours) {
            behaviour.physicsUpdate();
        }
    }

    public void triggerCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        for (GameObjectBehaviour behaviour : behaviours) {
            behaviour.onCollision(src, target, mtv);
        }
    }

    public void triggerRemove() {
        for (GameObjectBehaviour behaviour : behaviours) {
            behaviour.onDestroy();
        }
    }

    public <T extends GameObjectBehaviour> T getBehaviour(Class<T> type) {
        for (GameObjectBehaviour behaviour : behaviours) {
            if (type.isInstance(behaviour)) {
                return type.cast(behaviour);
            }
        }
        return null;
    }
}
