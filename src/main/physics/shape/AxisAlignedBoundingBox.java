package physics.shape;

import physics.Vec2D;

public class AxisAlignedBoundingBox extends ConvexShape {
    private Vec2D bottomLeft;
    private Vec2D topRight;

    public AxisAlignedBoundingBox(Vec2D bottomLeft, Vec2D topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    public Vec2D getBottomLeft() {
        return bottomLeft;
    }

    public Vec2D getTopRight() {
        return topRight;
    }

    public float getWidth() {
        return topRight.getX() - bottomLeft.getX();
    }

    public float getHeight() {
        return topRight.getY() - bottomLeft.getY();
    }

    public Vec2D getSize() {
        return new Vec2D(getWidth(), getHeight());
    }

    public Range getXRange() {
        return new Range(bottomLeft.getX(), topRight.getX());
    }

    public Range getYRange() {
        return new Range(bottomLeft.getY(), topRight.getY());
    }

    public boolean contains(Vec2D point) {
        return getXRange().contains(point.getX()) && getYRange().contains(point.getY());
    }

    public boolean contains(AxisAlignedBoundingBox box) {
        return getXRange().contains(box.getXRange()) && getYRange().contains(box.getYRange());
    }

    public boolean overlaps(AxisAlignedBoundingBox box) {
        return getXRange().overlaps(box.getXRange()) && getYRange().overlaps(box.getYRange());
    }

    public AxisAlignedBoundingBox getOverlap(AxisAlignedBoundingBox box) {
        if (!overlaps(box)) {
            return null;
        }
        return new AxisAlignedBoundingBox(
                new Vec2D(
                        Math.max(bottomLeft.getX(), box.getBottomLeft().getX()),
                        Math.max(bottomLeft.getY(), box.getBottomLeft().getY())
                ),
                new Vec2D(
                        Math.min(topRight.getX(), box.getTopRight().getX()),
                        Math.min(topRight.getY(), box.getTopRight().getY())
                )
        );
    }

    @Override
    public Vec2D[] getVertices() {
        return new Vec2D[] {
                bottomLeft,
                new Vec2D(bottomLeft.getX(), topRight.getY()),
                topRight,
                new Vec2D(topRight.getX(), bottomLeft.getY())
        };
    }

    @Override
    public void translate(Vec2D translation) {
        bottomLeft = bottomLeft.add(translation);
        topRight = topRight.add(translation);
    }

    @Override
    public ConvexShape copy() {
        return new AxisAlignedBoundingBox(bottomLeft.copy(), topRight.copy());
    }

    @Override
    public Vec2D[] getPerpAxis() {
        return new Vec2D[] {
                new Vec2D(0, 1),
                new Vec2D(1, 0)
        };
    }

    @Override
    public AxisAlignedBoundingBox getAABB() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AxisAlignedBoundingBox)) {
            return false;
        }
        AxisAlignedBoundingBox box = (AxisAlignedBoundingBox) obj;
        return bottomLeft.equals(box.getBottomLeft()) && topRight.equals(box.getTopRight());
    }
}
