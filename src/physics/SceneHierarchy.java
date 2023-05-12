package physics;

import physics.shape.ConvexShape;
import util.Pair;

import java.util.ArrayList;

// we love stepping on unity's toes
public class SceneHierarchy {
    private final ArrayList<SceneHierarchyNode> roots = new ArrayList<>();

    public SceneHierarchy() {
    }

    public SceneHierarchyNode addObject(PhysicsObject object) {
        SceneHierarchyNode node = new SceneHierarchyNode(object);
        roots.add(node);
        return node;
    }

    public boolean removeObject(PhysicsObject object) {
        for(SceneHierarchyNode root : roots) {
            if (root.removeObject(object)) return true;
        }
        return false;
    }

    public void addRoot(SceneHierarchyNode node) {
        roots.add(node);
    }

    public ArrayList<Pair<PhysicsObject, ConvexShape>> physicsUpdate(float dt) {
        signalBeginUpdate();
        ArrayList<Pair<PhysicsObject,ConvexShape>> list = new ArrayList<>();
        for(SceneHierarchyNode root : roots) {
            root.physicsUpdate(dt, list);
        }
        return list;
    }

    private void signalBeginUpdate() {
        // set updated to false for all physics objects
        for(SceneHierarchyNode root : roots) {
            root.signalBeginUpdate();
        }
    }
}
