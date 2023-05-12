package physics.broad;

import physics.PhysicsEngine;
import physics.shape.AxisAlignedBoundingBox;
import physics.Vec2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class QuadTreeNode<T> {
    private ArrayList<QuadTreeEntry<T>> contents = new ArrayList<>();
    private ArrayList<QuadTreeNode<T>> children;
    private final HashSet<Object> attributes = new HashSet<>();
    private final Vec2D bottomLeft;
    private final Vec2D size;

    public QuadTreeNode(Vec2D bLeft, Vec2D size) {
        children = null;
        this.bottomLeft = bLeft;
        this.size = size;
    }

    public Vec2D getBottomLeft() {
        return bottomLeft;
    }

    public Vec2D getSize() {
        return size;
    }

    public HashSet<Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(Object attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(Object attribute) {
        attributes.remove(attribute);
    }

    public boolean hasAttribute(Object attribute) {
        return attributes.contains(attribute);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public List<QuadTreeEntry<T>> getContents() {
        if (contents == null) return null;
        return Collections.unmodifiableList(contents);
    }

    public List<QuadTreeNode<T>> getChildren() {
        if (children == null) return null;
        return Collections.unmodifiableList(children);
    }

    private void split() {
        if (children != null) {
            return;
        }
        Vec2D halfSize = size.mult(0.5f);
        children = new ArrayList<>(4);
        children.add(new QuadTreeNode<>(bottomLeft, halfSize));
        children.add(new QuadTreeNode<>(bottomLeft.add(new Vec2D(halfSize.getX(), 0)), halfSize));
        children.add(new QuadTreeNode<>(bottomLeft.add(new Vec2D(0, halfSize.getY())), halfSize));
        children.add(new QuadTreeNode<>(bottomLeft.add(halfSize), halfSize));
    }

    public AxisAlignedBoundingBox getAABB() {
        return new AxisAlignedBoundingBox(bottomLeft, bottomLeft.add(size));
    }

    public void insert(T object, Vec2D objBleft, Vec2D objSize, int depthLeft, int maxSize) {
        if (depthLeft == 0) {
            return;
        }
        if (!getAABB().overlaps(new AxisAlignedBoundingBox(objBleft,objBleft.add(objSize)))) {
            return;
        }
        addAttribute(PhysicsEngine.AWAKE_ATTRIBUTE);
        if (children != null) { // not a leaf, expand tree
            for (int i = 0; i < 4; i++) {
                children.get(i).insert(object, objBleft, objSize, depthLeft - 1, maxSize);
            }
        } else { // currently a leaf, expand
            contents.add(new QuadTreeEntry<>(object, objBleft, objSize));
            if (contents.size() > maxSize && depthLeft > 0) {
                split();
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < contents.size(); i++) {
                    QuadTreeEntry<T> entry = contents.get(i);
                    for (int j = 0; j < 4; j++) {
                        children.get(j).insert(entry.getObject(), entry.getBottomLeft(), entry.getSize(), depthLeft - 1, maxSize);
                    }
                }
                contents.clear();
                contents = null;
            }
        }
    }

    public ArrayList<QuadTreeEntry<T>> findCloseObjects(Vec2D objBleft, Vec2D objSize) {
        ArrayList<QuadTreeEntry<T>> result = new ArrayList<>();
        if (children != null) {
            for (int i = 0; i < 4; i++) {
                result.addAll(children.get(i).findCloseObjects(objBleft, objSize));
            }
        } else {
            for (QuadTreeEntry<T> entry : contents) {
                if (new AxisAlignedBoundingBox(objBleft, objBleft.add(objSize)).overlaps(
                        new AxisAlignedBoundingBox(entry.getBottomLeft(), entry.getBottomLeft().add(entry.getSize())))) {
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public void remove(T object, Vec2D objBleft, Vec2D objSize) {
        if (!getAABB().overlaps(new AxisAlignedBoundingBox(objBleft,objBleft.add(objSize)))) {
            return;
        }
        if (children != null) {
            boolean shouldBeAwake = false;
            for (int i = 0; i < 4; i++) {
                children.get(i).remove(object, objBleft, objSize);
                if (children.get(i).hasAttribute(PhysicsEngine.AWAKE_ATTRIBUTE)) shouldBeAwake = true;
            }
            if (!shouldBeAwake) removeAttribute(PhysicsEngine.AWAKE_ATTRIBUTE);
        } else {
            for (int i = 0; i < contents.size(); i++) {
                if (contents.get(i).getObject().equals(object)) {
                    contents.remove(i);
                    return;
                }
            }
            if (contents.size() == 0) removeAttribute(PhysicsEngine.AWAKE_ATTRIBUTE);
        }
    }
}
