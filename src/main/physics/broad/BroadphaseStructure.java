package physics.broad;

import physics.shape.ConvexShape;

import java.awt.*;
import java.util.ArrayList;

public interface BroadphaseStructure<T> {
    void insert(T object, ConvexShape shape);

    void remove(T object, ConvexShape shape);

    void update(T object, ConvexShape oldShape, ConvexShape newShape);

    void render(Graphics2D g, Color c);

    ArrayList<T> findCloseObjects(ConvexShape shape);
}
