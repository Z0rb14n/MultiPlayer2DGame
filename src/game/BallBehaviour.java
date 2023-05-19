package game;

import engine.GameObject;
import engine.GameObjectBehaviour;
import engine.PhysicsBehaviour;
import engine.SceneHierarchyNode;
import physics.Vec2D;

public class BallBehaviour implements GameObjectBehaviour {
    private final GameObject go;
    private final SceneHierarchyNode parent;
    private int counter = 0;
    public BallBehaviour(GameObject go, SceneHierarchyNode parent) {
        this.go = go;
        this.parent = parent;
    }
    @Override
    public void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        counter++;
        GameLogger.getDefault().log(counter + "BOING","DEBUG");
        if (counter > 10) {
            parent.removeObject(go);
        }
    }
}
