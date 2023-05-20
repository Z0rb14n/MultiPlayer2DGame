package engine;

import java.util.ArrayList;
import java.util.Collections;

public class SceneHierarchyNode {
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private final ArrayList<SceneHierarchyNode> children = new ArrayList<>();

    public SceneHierarchyNode(GameObject... objects) {
        Collections.addAll(this.objects, objects);
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void addChild(SceneHierarchyNode node) {
        children.add(node);
    }

    public boolean removeObject(GameObject object) {
        boolean removed = objects.remove(object);
        if (removed) {
            object.triggerRemove();
            return true;
        }
        for (SceneHierarchyNode child : children) {
            removed = child.removeObject(object);
            if (removed) return true;
        }
        return false;
    }

    public void render(java.awt.Graphics2D g) {
        for (GameObject object : objects) {
            ArrayList<RendererBehaviour> renderers = object.getBehaviours(RendererBehaviour.class);
            for (RendererBehaviour renderer : renderers) {
                renderer.render(g);
            }
        }
        for (SceneHierarchyNode child : children) {
            child.render(g);
        }
    }

    public void update() {
        for (GameObject object : objects) {
            object.update();
        }
        for (SceneHierarchyNode child : children) {
            child.update();
        }
    }
}
