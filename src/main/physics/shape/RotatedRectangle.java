package physics.shape;

import physics.Vec2D;

public class RotatedRectangle extends ConvexShape {
    private final Vec2D[] precomputedVertices = new Vec2D[4];
    private Vec2D center;
    private float width;
    private float height;
    private float angle;

    public RotatedRectangle(Vec2D center, float width, float height) {
        this(center, width, height, 0);
    }

    public RotatedRectangle(Vec2D center, float width, float height, float angle) {
        this.center = center;
        this.width = width;
        this.height = height;
        this.angle = angle;
        computeVertices();
    }

    private void computeVertices() {
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        precomputedVertices[0] = new Vec2D(-halfWidth, -halfHeight).rotated(angle);
        precomputedVertices[1] = new Vec2D(halfWidth, -halfHeight).rotated(angle);
        precomputedVertices[2] = new Vec2D(halfWidth, halfHeight).rotated(angle);
        precomputedVertices[3] = new Vec2D(-halfWidth, halfHeight).rotated(angle);
    }

    public Vec2D getCenter() {
        return center;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getAngle() {
        return angle;
    }

    public void setWidth(float width) {
        this.width = width;
        computeVertices();
    }

    public void setHeight(float height) {
        this.height = height;
        computeVertices();
    }

    public void setAngle(float angle) {
        this.angle = angle;
        computeVertices();
    }

    public void rotate(float angle) {
        this.angle += angle;
        computeVertices();
    }

    @Override
    public Vec2D[] getPerpAxis() {
        // rectangles only have 2 perpendicular axes
        return new Vec2D[] {
                new Vec2D(0, 1).rotated(angle),
                new Vec2D(1, 0).rotated(angle)
        };
    }

    @Override
    public Vec2D[] getVertices() {
        Vec2D[] vertices = new Vec2D[4];
        for (int i = 0; i < 4; i++) {
            vertices[i] = precomputedVertices[i].add(center);
        }
        return vertices;
    }

    @Override
    public void translate(Vec2D translation) {
        center = center.add(translation);
    }

    @Override
    public ConvexShape copy() {
        return new RotatedRectangle(center.copy(), width, height, angle);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RotatedRectangle)) {
            return false;
        }
        RotatedRectangle other = (RotatedRectangle) obj;
        return center.equals(other.center) && width == other.width && height == other.height && angle == other.angle;
    }
}
