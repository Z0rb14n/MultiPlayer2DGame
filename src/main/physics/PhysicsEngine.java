package physics;

import engine.PhysicsBehaviour;
import physics.broad.BroadphaseStructure;
import physics.broad.QuadTree;
import physics.narrow.SATTest;
import physics.shape.ConvexShape;
import util.Pair;

import java.util.ArrayList;

public class PhysicsEngine {
    public static final Object AWAKE_ATTRIBUTE = "a";
    // super f--ked
    private final ArrayList<PhysicsBehaviour> physicsObjects = new ArrayList<>();
    private final BroadphaseStructure<PhysicsBehaviour> broadphaseStructure;
    private final ArrayList<PhysicsBehaviour> removalQueue = new ArrayList<>();

    public PhysicsEngine(BroadphaseStructure<PhysicsBehaviour> structure) {
        broadphaseStructure = structure;
    }

    public PhysicsEngine(Vec2D dim) {
        this(dim, Vec2D.ZERO);
    }

    public PhysicsEngine(Vec2D dim, Vec2D bottomLeft) {

        this(new QuadTree<>(dim, bottomLeft, 5, 6));
    }

    public BroadphaseStructure<PhysicsBehaviour> getBroadphaseStructure() {
        return broadphaseStructure;
    }

    public void add(PhysicsBehaviour object) {
        broadphaseStructure.insert(object, object.getTranslatedShape());
        physicsObjects.add(object);
    }

    public void remove(PhysicsBehaviour object) {
        removalQueue.add(object);
    }

    public void removeImmediate(PhysicsBehaviour object) {
        broadphaseStructure.remove(object, object.getTranslatedShape());
        physicsObjects.remove(object);
    }


    public void update(float dt) {
        for (PhysicsBehaviour object : removalQueue) {
            removeImmediate(object);
        }
        removalQueue.clear();
        ArrayList<Pair<PhysicsBehaviour, ConvexShape>> objectUpdateQueue = new ArrayList<>();
        for (PhysicsBehaviour object : physicsObjects) {
            ConvexShape result = object.physicsUpdate(dt);
            if (result != null) {
                objectUpdateQueue.add(new Pair<>(object, result));
            }
        }
        for(Pair<PhysicsBehaviour,ConvexShape> object : objectUpdateQueue) {
            broadphaseStructure.update(object.getFirst(), object.getSecond(), object.getFirst().getTranslatedShape());
        }
        objectUpdateQueue.clear();
        updateColStep();
        for (PhysicsBehaviour object : removalQueue) {
            removeImmediate(object);
        }
        removalQueue.clear();
    }

    private void updateColStep() {
        for (PhysicsBehaviour behaviour : physicsObjects) {
            if (behaviour.isStationary() || !behaviour.isAwake()) continue;
            ArrayList<PhysicsBehaviour> behaviours = broadphaseStructure.findCloseObjects(behaviour.getTranslatedShape());

            for (PhysicsBehaviour behaviour2 : behaviours) {
                if (behaviour != behaviour2) {
                    handleCollision(behaviour, behaviour2);
                }
            }
        }
    }

    private void handleCollision(PhysicsBehaviour obj1, PhysicsBehaviour obj2) {
        if ((obj1.getCollisionMask() & obj2.getCollisionMask()) == 0) return;
        Vec2D mtv = SATTest.getMTV(obj1.getTranslatedShape(), obj2.getTranslatedShape());
        if (mtv == null) return;
        // https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf
        float m1 = obj1.getMass();
        float m2 = obj2.getMass();
        Vec2D v1 = obj1.getVelocity();
        Vec2D v2 = obj2.getVelocity();
        float v1d = v1.dot(mtv.normalize());
        float v2d = v2.dot(mtv.normalize());
        Vec2D v1p = v1.sub(mtv.normalize().mult(v1d));
        Vec2D v2p = v2.sub(mtv.normalize().mult(v2d));
        float cor = (float)Math.sqrt(obj1.getCoefOfRestitution() * obj2.getCoefOfRestitution());
        // TODO USE IMPULSE RATHER THAN VELOCITY LOL
        // v1f = (m1 * v1 + m2 * v2 + (m2 * cor * (v2 - v1))) / (m1 + m2)
        float v1f = (m1 * v1d + m2 * v2d + (m2 * cor * (v2d - v1d))) / (m1 + m2);
        // v2f = (m1 * v1 + m2 * v2 + (m1 * cor * (v1 - v2))) / (m1 + m2)
        float v2f = (m1 * v1d + m2 * v2d + (m1 * cor * (v1d - v2d))) / (m1 + m2);
        if (obj1.isStationary()) {
            v2f = v2d * -cor;
            obj2.translate(mtv.mult(-1));
        } else if (obj2.isStationary()) {
            v1f = v1d * -cor;
            obj1.translate(mtv);
        } else {
            obj1.translate(mtv.mult(0.5f));
            obj2.translate(mtv.mult(-0.5f));
        }

        Vec2D v1fvec = obj1.isStationary() ? Vec2D.ZERO :
                v1p.add(mtv.normalize().mult(v1f));
        Vec2D v2fvec = obj2.isStationary() ? Vec2D.ZERO :
                v2p.add(mtv.normalize().mult(v2f));
        obj1.setVelocity(v1fvec);
        obj2.setVelocity(v2fvec);
        obj1.onCollision(obj2, mtv);
        obj2.onCollision(obj1, mtv);
    }
}
