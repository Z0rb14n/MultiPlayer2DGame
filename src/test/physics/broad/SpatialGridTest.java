package physics.broad;

import engine.PhysicsBehaviour;
import org.junit.jupiter.api.Test;
import physics.Vec2D;
import physics.shape.Circle;
import util.Pair;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SpatialGridTest {
    @Test
    public void testLocations() {
        SpatialGrid<PhysicsBehaviour> grid = new SpatialGrid<>(new Vec2D(10,10));
        ArrayList<Pair<Integer, Integer>> relevant = grid.getRelevantCellLocations(new Circle(new Vec2D(10,10), 5));
        assertEquals(4, relevant.size());
    }
}
