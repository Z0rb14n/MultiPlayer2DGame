package physics.shape;

import physics.Vec2D;

public class Triangle extends ConvexShape {
    private Vec2D vertex1;
    private Vec2D vertex2;
    private Vec2D vertex3;

    public Triangle(Vec2D vertex1, Vec2D vertex2, Vec2D vertex3) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
    }

    public void translate(Vec2D translation) {
        vertex1 = vertex1.add(translation);
        vertex2 = vertex2.add(translation);
        vertex3 = vertex3.add(translation);
    }

    @Override
    public Vec2D[] getPerpAxis() {
        return new Vec2D[] {
            vertex2.sub(vertex1).perp().normalize(),
            vertex3.sub(vertex2).perp().normalize(),
            vertex1.sub(vertex3).perp().normalize()
        };
    }

    @Override
    public ConvexShape copy() {
        return new Triangle(vertex1, vertex2, vertex3);
    }

    @Override
    public Vec2D[] getVertices() {
        return new Vec2D[] {
            vertex1,
            vertex2,
            vertex3
        };
    }
}
