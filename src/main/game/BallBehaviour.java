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
    public void physicsUpdate() {
        Vec2D pos = go.getPosition();
        //System.out.println(pos);
        if (pos.getX() < 10 || pos.getX() > GameController.GAME_WIDTH-10 || pos.getY() < 10 || pos.getY() > GameController.GAME_HEIGHT-10) {
            GameLogger.getDefault().log("BALL OUT OF BOUNDS: " + pos,"IGNORE");
        }
    }

    @Override
    public void onCollision(PhysicsBehaviour src, PhysicsBehaviour target, Vec2D mtv) {
        counter++;
        //GameLogger.getDefault().log("BOUNCE COUNT " + counter,"DEBUG");
        if (counter > 10) {
            parent.removeObject(go);
        }
    }
}
