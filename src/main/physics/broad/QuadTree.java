package physics.broad;

import physics.shape.AxisAlignedBoundingBox;
import physics.shape.ConvexShape;
import physics.Vec2D;

import java.util.ArrayList;

// https://research.ncl.ac.uk/game/mastersdegree/gametechnologies/physicstutorials/6accelerationstructures/Physics%20-%20Spatial%20Acceleration%20Structures.pdf
public class QuadTree<T> implements BroadphaseStructure<T> {
    private final QuadTreeNode<T> root;
    private final Vec2D bLeft;
    private final int maxDepth;
    private final int maxSize;

    public QuadTreeNode<T> getRoot() {
        return root;
    }

    public QuadTree(Vec2D size, int maxDepth, int maxSize) {
        bLeft = Vec2D.ZERO;
        root = new QuadTreeNode<>(bLeft, size);
        this.maxDepth = maxDepth;
        this.maxSize = maxSize;
    }

    public QuadTree(Vec2D size, Vec2D bleft, int maxDepth, int maxSize) {
        this.bLeft = bleft;
        root = new QuadTreeNode<>(bLeft, size);
        this.maxDepth = maxDepth;
        this.maxSize = maxSize;
    }

    public void insert(T object, ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        insert(object, box.getBottomLeft(), box.getSize());
    }

    public void insert(T object, Vec2D objBleft, Vec2D objSize) {
        if (objBleft.getX() + objSize.getX() < bLeft.getX() || objBleft.getY() + objSize.getY() < bLeft.getY()) {
            System.err.println("Object " + object + " placed in invalid position " + objBleft + " with size " + objSize);
            objBleft = new Vec2D(Math.max(objBleft.getX(), 0), Math.max(objBleft.getY(), 0));
        }
        root.insert(object, objBleft, objSize, maxDepth, maxSize);
    }

    public void forceRemove(T object) {
        root.forceRemove(object);
    }

    public void remove(T object, ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        remove(object, box.getBottomLeft(), box.getSize());
    }

    public void remove(T object, Vec2D objBleft, Vec2D objSize) {
        root.remove(object, objBleft, objSize);
    }

    public void update(T object, ConvexShape pre, ConvexShape post) {
        AxisAlignedBoundingBox preBox = pre.getAABB();
        AxisAlignedBoundingBox postBox = pre.getAABB();
        remove(object, preBox.getBottomLeft(), preBox.getSize());
        insert(object, postBox.getBottomLeft(), postBox.getSize());
    }

    public ArrayList<T> findCloseObjects(ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        return findCloseObjects(box.getBottomLeft(), box.getSize());
    }

    public ArrayList<T> findCloseObjects(Vec2D objBleft, Vec2D objSize) {
        ArrayList<QuadTreeEntry<T>> entries = root.findCloseObjects(objBleft, objSize);
        ArrayList<T> objects = new ArrayList<>();
        for (QuadTreeEntry<T> entry : entries) {
            objects.add(entry.getObject());
        }
        return objects;
    }

    public ArrayList<QuadTreeEntry<T>> findCloseEntries(ConvexShape shape) {
        AxisAlignedBoundingBox box = shape.getAABB();
        return findCloseEntries(box.getBottomLeft(), box.getSize());
    }

    public ArrayList<QuadTreeEntry<T>> findCloseEntries(Vec2D objBleft, Vec2D objSize) {
        return root.findCloseObjects(objBleft, objSize);
    }
}
