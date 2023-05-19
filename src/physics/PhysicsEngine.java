package physics;

import engine.PhysicsBehaviour;
import physics.broad.QuadTree;
import physics.broad.QuadTreeEntry;
import physics.broad.QuadTreeNode;
import physics.narrow.SATTest;
import physics.shape.ConvexShape;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static final Object AWAKE_ATTRIBUTE = "a";
    // super f--ked
    private final ArrayList<PhysicsBehaviour> list = new ArrayList<>();
    private final QuadTree<PhysicsBehaviour> tree;
    private final ArrayList<PhysicsBehaviour> removalQueue = new ArrayList<>();

    public PhysicsEngine(Vec2D dim) {
        this(dim, Vec2D.ZERO);
    }

    public PhysicsEngine(Vec2D dim, Vec2D bottomLeft) {
        tree = new QuadTree<>(dim, bottomLeft, 5, 6);
    }

    public QuadTree<PhysicsBehaviour> getTree() {
        return tree;
    }

    public void add(PhysicsBehaviour object) {
        tree.insert(object, object.getTranslatedShape());
        list.add(object);
    }

    public void remove(PhysicsBehaviour object) {
        removalQueue.add(object);
    }

    public void removeImmediate(PhysicsBehaviour object) {
        tree.remove(object, object.getTranslatedShape());
        list.remove(object);
    }

    public void removeImmediateOOB(PhysicsBehaviour object) {
        tree.forceRemove(object);
        list.remove(object);
    }


    public void update(float dt) {
        for (PhysicsBehaviour object : removalQueue) {
            removeImmediate(object);
        }
        removalQueue.clear();
        ArrayList<Pair<PhysicsBehaviour, ConvexShape>> objectUpdateQueue = new ArrayList<>();
        for (PhysicsBehaviour object : list) {
            ConvexShape result = object.physicsUpdate(dt);
            if (result != null) {
                objectUpdateQueue.add(new Pair<>(object, result));
            }
        }
        for(Pair<PhysicsBehaviour,ConvexShape> object : objectUpdateQueue) {
            tree.update(object.getFirst(), object.getSecond(), object.getFirst().getTranslatedShape());
        }
        objectUpdateQueue.clear();
        updateColStep(tree.getRoot());
        for (PhysicsBehaviour object : removalQueue) {
            removeImmediate(object);
        }
        removalQueue.clear();
    }

    private void updateColStep(QuadTreeNode<PhysicsBehaviour> node) {
        if (!node.hasAttribute(AWAKE_ATTRIBUTE)) return;
        if (node.getChildren() == null) {
            for (QuadTreeEntry<PhysicsBehaviour> obj1 : node.getContents()) {
                if (obj1.getObject().isStationary() || !obj1.getObject().isAwake()) continue;
                for (QuadTreeEntry<PhysicsBehaviour> obj2 : node.getContents()) {
                    if (obj1 != obj2) {
                        handleCollision(obj1, obj2);
                    }
                }
            }
        } else {
            List<QuadTreeNode<PhysicsBehaviour>> children = node.getChildren();
            for (QuadTreeNode<PhysicsBehaviour> child : children) {
                updateColStep(child);
            }
        }
    }

    private void handleCollision(QuadTreeEntry<PhysicsBehaviour> obj1, QuadTreeEntry<PhysicsBehaviour> obj2) {
        if ((obj1.getObject().getCollisionMask() & obj2.getObject().getCollisionMask()) == 0) return;
        Vec2D mtv = SATTest.getMTV(obj1.getObject().getTranslatedShape(), obj2.getObject().getTranslatedShape());
        if (mtv == null) return;
        // https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf
        float m1 = obj1.getObject().getMass();
        float m2 = obj2.getObject().getMass();
        Vec2D v1 = obj1.getObject().getVelocity();
        Vec2D v2 = obj2.getObject().getVelocity();
        float v1d = v1.dot(mtv.normalize());
        float v2d = v2.dot(mtv.normalize());
        Vec2D v1p = v1.sub(mtv.normalize().mult(v1d));
        Vec2D v2p = v2.sub(mtv.normalize().mult(v2d));
        float cor = (float)Math.sqrt(obj1.getObject().getCoefOfRestitution() * obj2.getObject().getCoefOfRestitution());
        // v1f = (m1 * v1 + m2 * v2 + (m2 * cor * (v2 - v1))) / (m1 + m2)
        float v1f = (m1 * v1d + m2 * v2d + (m2 * cor * (v2d - v1d))) / (m1 + m2);
        // v2f = (m1 * v1 + m2 * v2 + (m1 * cor * (v1 - v2))) / (m1 + m2)
        float v2f = (m1 * v1d + m2 * v2d + (m1 * cor * (v1d - v2d))) / (m1 + m2);
        if (obj1.getObject().isStationary()) {
            v2f = v2d * -cor;
            obj2.getObject().translate(mtv);
        } else if (obj2.getObject().isStationary()) {
            v1f = v1d * -cor;
            obj1.getObject().translate(mtv);
        } else {
            obj1.getObject().translate(mtv.mult(0.5f));
            obj2.getObject().translate(mtv.mult(-0.5f));
        }

        Vec2D v1fvec = obj1.getObject().isStationary() ? Vec2D.ZERO :
                v1p.add(mtv.normalize().mult(v1f));
        Vec2D v2fvec = obj2.getObject().isStationary() ? Vec2D.ZERO :
                v2p.add(mtv.normalize().mult(v2f));
        obj1.getObject().setVelocity(v1fvec);
        obj2.getObject().setVelocity(v2fvec);
        obj1.getObject().onCollision(obj2.getObject(), mtv);
        obj2.getObject().onCollision(obj1.getObject(), mtv);
    }
}
