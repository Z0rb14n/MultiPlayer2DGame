package physics.broad;

import physics.shape.AxisAlignedBoundingBox;
import physics.shape.ConvexShape;
import physics.Vec2D;

import java.util.ArrayList;

// https://research.ncl.ac.uk/game/mastersdegree/gametechnologies/physicstutorials/6accelerationstructures/Physics%20-%20Spatial%20Acceleration%20Structures.pdf
public class QuadTree<T> {
    private final QuadTreeNode<T> root;
    private final int maxDepth;
    private final int maxSize;

    public QuadTreeNode<T> getRoot() {
        return root;
    }

    public QuadTree(Vec2D size, int maxDepth, int maxSize) {
        root = new QuadTreeNode<>(new Vec2D(0, 0), size);
        this.maxDepth = maxDepth;
        this.maxSize = maxSize;
    }

    public void insert(T object, ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        insert(object, box.getBottomLeft(), box.getSize());
    }

    public void insert(T object, Vec2D objBleft, Vec2D objSize) {
        root.insert(object, objBleft, objSize, maxDepth, maxSize);
    }

    public void remove(T object) {
        root.remove(object);
    }

    public void update(T object, ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        update(object, box.getBottomLeft(), box.getSize());
    }

    private void update(T object, Vec2D bottomLeft, Vec2D size) {
        remove(object);
        insert(object, bottomLeft, size);
    }

    public ArrayList<QuadTreeEntry<T>> findCloseObjects(ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        return findCloseObjects(box.getBottomLeft(), box.getSize());
    }

    public ArrayList<QuadTreeEntry<T>> findCloseObjects(Vec2D objBleft, Vec2D objSize) {
        return root.findCloseObjects(objBleft, objSize);
    }
}
