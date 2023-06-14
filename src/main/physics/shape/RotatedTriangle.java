package physics.shape;

import physics.Vec2D;

public class RotatedTriangle extends ConvexShape {
    private final Vec2D[] precomputedVertices = new Vec2D[3];
    private final Vec2D[] precomputedPerpAxes = new Vec2D[3];
    private final Vec2D[] vertices = new Vec2D[3];
    private Vec2D center;
    private float angle;

    public RotatedTriangle(Vec2D[] vertices) {
        this(vertices,Vec2D.ZERO,0);
    }

    public RotatedTriangle(Vec2D[] vertices, Vec2D center) {
        this(vertices,center,0);
    }

    public RotatedTriangle(Vec2D[] vertices, float angle) {
        this(vertices,Vec2D.ZERO,angle);
    }

    public RotatedTriangle(Vec2D[] vertices, Vec2D center, float angle) {
        System.arraycopy(vertices,0,this.vertices,0,3);
        this.center = center;
        this.angle = angle;
        computeVertices();
    }

    private void computeVertices() {
        precomputedVertices[0] = vertices[0].rotated(angle);
        precomputedVertices[1] = vertices[1].rotated(angle);
        precomputedVertices[2] = vertices[2].rotated(angle);

        precomputedPerpAxes[0] = precomputedVertices[1].sub(precomputedVertices[0]).perp().normalize();
        precomputedPerpAxes[1] = precomputedVertices[2].sub(precomputedVertices[1]).perp().normalize();
        precomputedPerpAxes[2] = precomputedVertices[0].sub(precomputedVertices[2]).perp().normalize();
    }

    public Vec2D getCenter() {
        return center;
    }

    public float getAngle() {
        return angle;
    }

    public void rotate(float angle) {
        this.angle += angle;
        computeVertices();
    }

    public void setAngle(float angle) {
        this.angle = angle;
        computeVertices();
    }

    @Override
    public Vec2D[] getVertices() {
        Vec2D[] vertices = new Vec2D[3];
        for (int i = 0; i < 3; i++) {
            vertices[i] = precomputedVertices[i].add(center);
        }
        return vertices;
    }

    @Override
    public Vec2D[] getPerpAxis() {
        return precomputedPerpAxes;
    }

    @Override
    public void translate(Vec2D translation) {
        center = center.add(translation);
    }

    @Override
    public ConvexShape copy() {
        return new RotatedTriangle(vertices,center,angle);
    }
}
