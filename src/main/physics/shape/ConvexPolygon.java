package physics.shape;

import physics.Vec2D;

public class ConvexPolygon extends ConvexShape {
    private final Vec2D[] vertices;

    public ConvexPolygon(Vec2D[] vertices) {
        this.vertices = new Vec2D[vertices.length];
        System.arraycopy(vertices, 0, this.vertices, 0, vertices.length);
    }

    @Override
    public Vec2D[] getVertices() {
        return vertices;
    }

    @Override
    public void translate(Vec2D translation) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertices[i].add(translation);
        }
    }

    @Override
    public ConvexShape copy() {
        return new ConvexPolygon(vertices);
    }
}
