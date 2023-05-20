package physics.shape;

import org.junit.jupiter.api.Test;
import physics.Vec2D;
import physics.narrow.SATTest;

import static game.GameController.GAME_HEIGHT;
import static game.GameController.GAME_WIDTH;
import static org.junit.jupiter.api.Assertions.*;

public class CircleTest {
    private static void assertVectorEquals(Vec2D expected, Vec2D actual, double delta) {
        if (expected == null) assertNull(actual);
        else {
            assertNotNull(actual);
            assertEquals(expected.getX(), actual.getX(), delta);
            assertEquals(expected.getY(), actual.getY(), delta);
        }
    }
    @Test
    public void testProjection() {
        Vec2D center = new Vec2D(500, 200);
        float radius = 10;
        Circle circle = new Circle(center, radius);
        Vec2D axis = new Vec2D(1, 0);
        Range range = circle.project(axis);
        assertEquals(range.getLower(), 490);
        assertEquals(range.getUpper(), 510);

        axis = new Vec2D(0, 1);
        range = circle.project(axis);
        assertEquals(range.getLower(), 190);
        assertEquals(range.getUpper(), 210);

        axis = new Vec2D(1, 1);

        range = circle.project(axis);
        assertEquals(range.getLower(), 350 * Math.sqrt(2)-10, 0.0001);
        assertEquals(range.getUpper(), 350 * Math.sqrt(2)+10,0.0001);
    }

    @Test
    public void testMTV() {
        AxisAlignedBoundingBox top = new AxisAlignedBoundingBox(new Vec2D(-500, -500), new Vec2D(GAME_WIDTH + 500, 20));
        AxisAlignedBoundingBox bot = new AxisAlignedBoundingBox(new Vec2D(-500, GAME_HEIGHT - 20), new Vec2D(GAME_WIDTH + 500, GAME_HEIGHT + 500));
        AxisAlignedBoundingBox left = new AxisAlignedBoundingBox(new Vec2D(-500, -500), new Vec2D(20, GAME_HEIGHT + 500));
        AxisAlignedBoundingBox right = new AxisAlignedBoundingBox(new Vec2D(GAME_WIDTH - 20, -500), new Vec2D(GAME_WIDTH + 500, GAME_HEIGHT + 500));

        Circle circle = new Circle(new Vec2D(20,20), 10);
        Vec2D mtv = SATTest.getMTV(circle, top);
        assertNotNull(mtv);
        assertVectorEquals(new Vec2D(0, 10), mtv, 0.0001);
        assertVectorEquals(mtv.mult(-1), SATTest.getMTV(top, circle), 0.0001);
    }
}
