package engine;

// we love stepping on unity's toes
public class SceneHierarchy {
    private final SceneHierarchyNode root = new SceneHierarchyNode();

    public SceneHierarchy() {
    }

    public SceneHierarchyNode getRoot() {
        return root;
    }

    public void addObject(GameObject object) {
        root.addObject(object);
    }
    public boolean removeObject(GameObject object) {
        return root.removeObject(object);
    }

    public void render(java.awt.Graphics2D g) {
        root.render(g);
    }

    public void update() {
        root.update();
    }
}
