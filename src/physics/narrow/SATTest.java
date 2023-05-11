package physics.narrow;

import physics.shape.Circle;
import physics.shape.ConvexShape;
import physics.shape.Range;
import physics.Vec2D;

// See: https://dyn4j.org/2010/01/sat/
// Also see: https://cs.brown.edu/courses/cs1971/lectures/lecture05.pdf
public class SATTest {
    public static boolean hasIntersection(ConvexShape shape1, ConvexShape shape2) {
        return getMTV(shape1, shape2) != null;
    }

    public static Vec2D getMTV(ConvexShape shape1, ConvexShape shape2) {
        Vec2D[] axis1 = shape1 instanceof Circle ? new Vec2D[]{((Circle) shape1).getSATAxis(shape2)} : shape1.getPerpAxis();
        Vec2D[] axis2 = shape2 instanceof Circle ? new Vec2D[]{((Circle) shape2).getSATAxis(shape1)} : shape2.getPerpAxis();
        float minOverlap = Float.MAX_VALUE;
        Vec2D minAxis = null;
        for (Vec2D axis : axis1) {
            Range p1 = shape1.project(axis);
            Range p2 = shape2.project(axis);
            float mtv1d = p1.getIntervalMTV(p2);
            if (Float.isNaN(mtv1d)) return null;
            if (Math.abs(mtv1d) < minOverlap) {
                minOverlap = Math.abs(mtv1d);
                minAxis = axis.mult(mtv1d);
            }
        }
        for (Vec2D axis : axis2) {
            Range p1 = shape1.project(axis);
            Range p2 = shape2.project(axis);
            float mtv1d = p1.getIntervalMTV(p2);
            if (Float.isNaN(mtv1d)) return null;
            if (Math.abs(mtv1d) < minOverlap) {
                minOverlap = Math.abs(mtv1d);
                minAxis = axis.mult(mtv1d);
            }
        }
        assert minAxis != null;
        return minAxis;
    }
}
