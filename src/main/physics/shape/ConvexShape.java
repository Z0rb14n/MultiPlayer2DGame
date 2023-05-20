package physics.shape;

import physics.Vec2D;

public abstract class ConvexShape {
    public abstract Vec2D[] getVertices();

    public abstract void translate(Vec2D translation);

    public abstract ConvexShape copy();

    public Range project(Vec2D axis) {
        Vec2D[] vertices = getVertices();
        float min = vertices[0].projNorm(axis);
        float max = min;
        for (int i = 1; i < vertices.length; i++) {
            float proj = vertices[i].projNorm(axis);
            if (proj < min) {
                min = proj;
            } else if (proj > max) {
                max = proj;
            }
        }
        return new Range(min, max);
    }

    public Vec2D[] getPerpAxis() {
        Vec2D[] vertices = getVertices();
        Vec2D[] axis = new Vec2D[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            axis[i] = vertices[i].sub(vertices[(i+1)%vertices.length]).perp().normalize();
        }
        return axis;
    }

    public AxisAlignedBoundingBox getAABB() {
        Vec2D[] vertices = getVertices();
        float minX = vertices[0].getX();
        float maxX = minX;
        float minY = vertices[0].getY();
        float maxY = minY;
        for (int i = 1; i < vertices.length; i++) {
            float x = vertices[i].getX();
            float y = vertices[i].getY();
            if (x < minX) {
                minX = x;
            } else if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            } else if (y > maxY) {
                maxY = y;
            }
        }
        return new AxisAlignedBoundingBox(new Vec2D(minX, minY), new Vec2D(maxX, maxY));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConvexShape) {
            ConvexShape shape = (ConvexShape) obj;
            Vec2D[] vertices = getVertices();
            Vec2D[] shapeVertices = shape.getVertices();
            if (vertices.length == shapeVertices.length) {
                for (int i = 0; i < vertices.length; i++) {
                    if (!vertices[i].equals(shapeVertices[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
