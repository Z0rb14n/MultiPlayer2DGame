package physics.shape;

import physics.Vec2D;

import java.util.Objects;

// See https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf
public class Circle extends ConvexShape {
    private float radius;
    private Vec2D center;

    public Circle(Vec2D center, float radius) {
        this.radius = radius;
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public Vec2D getCenter() {
        return center;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public Vec2D[] getVertices() {
        throw new UnsupportedOperationException("Vertices on circles :skull:");
    }

    @Override
    public void translate(Vec2D translation) {
        center = center.add(translation);
    }

    @Override
    public ConvexShape copy() {
        return new Circle(center, radius);
    }

    @Override
    public Range project(Vec2D axis) {
        float proj = center.projNorm(axis);
        return new Range(proj - radius, proj + radius);
    }

    /**
     * Returns the axis of the circle and the other shape for SAT.
     * Note that we need a special case for circles as it doesn't have a perpendicular axis.
     * See <a href="https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf">Brown University's 2D Game Engines Lecture 5</a> slide 26
     *
     * @param other Other convex shape, including circles
     * @return Axis to perform SAT on
     */
    public Vec2D getSATAxis(ConvexShape other) {
        if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            Vec2D diff = otherCircle.center.sub(center);
            return diff.normalize();
        } else {
            Vec2D[] vertices = other.getVertices();
            Vec2D closest = vertices[0];
            float minDist = (center.sub(closest)).sqMag();
            for (int i = 1; i < vertices.length; i++) {
                Vec2D vertex = vertices[i];
                float dist = (center.sub(vertex)).sqMag();
                if (dist < minDist) {
                    minDist = dist;
                    closest = vertex;
                }
            }
            return closest.sub(center).normalize();
        }
    }

    @Override
    public Vec2D[] getPerpAxis() {
        throw new UnsupportedOperationException("Perpendicular axis on circles :skull:");
    }

    @Override
    public AxisAlignedBoundingBox getAABB() {
        return new AxisAlignedBoundingBox(center.sub(radius, radius), center.add(radius, radius));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Circle) {
            Circle other = (Circle) obj;
            return radius == other.radius && center.equals(other.center);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, center);
    }
}
