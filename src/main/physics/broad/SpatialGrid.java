package physics.broad;

import physics.Vec2D;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.ConvexShape;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class SpatialGrid<T> implements BroadphaseStructure<T> {
    private final Vec2D cellSize;
    private final HashMap<CellPosition, ArrayList<T>> cells = new HashMap<>();

    public SpatialGrid(Vec2D cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    public void insert(T object, ConvexShape shape) {
        AxisAlignedBoundingBox aabb = shape.getAABB();
        int x1 = (int) Math.floor(aabb.getBottomLeft().getX() / cellSize.getX());
        int y1 = (int) Math.floor(aabb.getBottomLeft().getY() / cellSize.getY());
        int x2 = (int) Math.ceil(aabb.getTopRight().getX() / cellSize.getX());
        int y2 = (int) Math.ceil(aabb.getTopRight().getY() / cellSize.getY());
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                CellPosition pos = new CellPosition(x,y);
                if(!cells.containsKey(pos)) {
                    cells.put(pos, new ArrayList<>());
                }
                cells.get(pos).add(object);
            }
        }
    }

    @Override
    public void remove(T object, ConvexShape shape) {
        AxisAlignedBoundingBox aabb = shape.getAABB();
        int x1 = (int) Math.floor(aabb.getBottomLeft().getX() / cellSize.getX());
        int y1 = (int) Math.floor(aabb.getBottomLeft().getY() / cellSize.getY());
        int x2 = (int) Math.ceil(aabb.getTopRight().getX() / cellSize.getX());
        int y2 = (int) Math.ceil(aabb.getTopRight().getY() / cellSize.getY());
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                CellPosition pos = new CellPosition(x,y);
                ArrayList<T> objects = cells.get(pos);
                if (objects != null) {
                    objects.remove(object);
                    if (objects.size() == 0) {
                        cells.remove(pos);
                    }
                }
            }
        }

    }

    @Override
    public void update(T object, ConvexShape oldShape, ConvexShape newShape) {
        remove(object, oldShape);
        insert(object, newShape);
    }

    @Override
    public ArrayList<T> findCloseObjects(ConvexShape shape) {

        AxisAlignedBoundingBox aabb = shape.getAABB();
        int x1 = (int) Math.floor(aabb.getBottomLeft().getX() / cellSize.getX());
        int y1 = (int) Math.floor(aabb.getBottomLeft().getY() / cellSize.getY());
        int x2 = (int) Math.ceil(aabb.getTopRight().getX() / cellSize.getX());
        int y2 = (int) Math.ceil(aabb.getTopRight().getY() / cellSize.getY());
        ArrayList<T> result = new ArrayList<>();
        HashSet<T> seen = new HashSet<>();
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                CellPosition pos = new CellPosition(x,y);
                if(cells.containsKey(pos)) {
                    for(T obj : cells.get(pos)) {
                        if(!seen.contains(obj)) {
                            seen.add(obj);
                            result.add(obj);
                        }
                    }
                }
            }
        }

        return result;
    }

    public void render(Graphics2D g, Color color) {
        g.setColor(color);
        for(CellPosition pos : cells.keySet()) {
            g.drawRect(pos.x * (int)cellSize.getX(), pos.y * (int)cellSize.getY(), (int)cellSize.getX(), (int)cellSize.getY());
        }
    }

    private static class CellPosition {
        private final int x;
        private final int y;

        public CellPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof CellPosition) {
                CellPosition other = (CellPosition) o;
                return other.x == x && other.y == y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x,y);
            // Copilot's autogen: looks kinda scuffed
            //return x + y * 1000;
        }
    }
}
