package physics.broad;

import physics.Vec2D;

public class QuadTreeEntry<T> {
    private final T object;
    private final Vec2D bottomLeft;
    private final Vec2D size;

    public QuadTreeEntry(T object, Vec2D bottomLeft, Vec2D size) {
        this.object = object;
        this.bottomLeft = bottomLeft;
        this.size = size;
    }

    public T getObject() {
        return object;
    }

    public Vec2D getBottomLeft() {
        return bottomLeft;
    }

    public Vec2D getSize() {
        return size;
    }
}
