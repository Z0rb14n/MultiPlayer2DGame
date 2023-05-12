package physics;

import physics.shape.ConvexShape;
import util.Pair;

import java.util.ArrayList;
import java.util.Collections;

public class SceneHierarchyNode {
    private final ArrayList<PhysicsObject> objects = new ArrayList<>();
    private final ArrayList<SceneHierarchyNode> children = new ArrayList<>();

    public SceneHierarchyNode(PhysicsObject... objects) {
        Collections.addAll(this.objects, objects);
    }

    public SceneHierarchyNode addObject(PhysicsObject object) {
        SceneHierarchyNode node = new SceneHierarchyNode(object);
        children.add(node);
        return node;
    }

    public void addChild(SceneHierarchyNode node) {
        children.add(node);
    }

    public void physicsUpdate(float dt, ArrayList<Pair<PhysicsObject, ConvexShape>> list) {
        for (PhysicsObject object : objects) {
            if (object != null) {
                ConvexShape prev = object.getTranslatedShape().copy();
                boolean moved = object.physicsUpdate(dt);
                if (moved) {
                    list.add(new Pair<>(object, prev));
                }
            }
        }
        for (SceneHierarchyNode child : children) {
            child.physicsUpdate(dt, list);
        }
    }

    public boolean removeObject(PhysicsObject object) {
        boolean removed = objects.remove(object);
        if (removed) return true;
        for (SceneHierarchyNode child : children) {
            removed = child.removeObject(object);
            if (removed) return true;
        }
        return false;
    }

    void signalBeginUpdate() {
        for (PhysicsObject object : objects) {
            object.updated = false;
        }
        for (SceneHierarchyNode child : children) {
            child.signalBeginUpdate();
        }
    }
}
