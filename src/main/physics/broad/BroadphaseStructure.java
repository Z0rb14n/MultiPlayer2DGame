package physics.broad;

import physics.shape.ConvexShape;

import java.util.ArrayList;

public interface BroadphaseStructure<T> {
    void insert(T object, ConvexShape shape);

    void remove(T object, ConvexShape shape);

    ArrayList<T> findCloseObjects(ConvexShape shape);
}
