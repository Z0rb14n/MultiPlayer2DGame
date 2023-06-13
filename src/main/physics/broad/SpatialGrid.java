package physics.broad;

import physics.Vec2D;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.ConvexShape;
import util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpatialGrid<T> implements BroadphaseStructure<T> {
    private final Vec2D cellSize;
    private final HashMap<Pair<Integer,Integer>, ArrayList<T>> cells = new HashMap<>();
    private final HashMap<T, ArrayList<Pair<Integer, Integer>>> objectToCells = new HashMap<>();

    public SpatialGrid(Vec2D cellSize) {
        this.cellSize = cellSize;
    }

    public ArrayList<Pair<Integer, Integer>> getRelevantCellLocations(ConvexShape shape) {
        AxisAlignedBoundingBox aabb = shape.getAABB();
        int x1 = (int) Math.floor(aabb.getBottomLeft().getX() / cellSize.getX());
        int y1 = (int) Math.floor(aabb.getBottomLeft().getY() / cellSize.getY());
        int x2 = (int) Math.floor(aabb.getTopRight().getX() / cellSize.getX());
        int y2 = (int) Math.floor(aabb.getTopRight().getY() / cellSize.getY());
        ArrayList<Pair<Integer, Integer>> cells = new ArrayList<>();
        for(int x = x1; x <= x2; x++) {
            for(int y = y1; y <= y2; y++) {
                cells.add(new Pair<>(x, y));
            }
        }
        return cells;
    }

    @Override
    public void insert(T object, ConvexShape shape) {
        ArrayList<Pair<Integer, Integer>> locations = getRelevantCellLocations(shape);
        for (Pair<Integer, Integer> location : locations) {
            if (!cells.containsKey(location)) {
                cells.put(location, new ArrayList<>());
            }
            cells.get(location).add(object);
            if (!objectToCells.containsKey(object)) {
                objectToCells.put(object, new ArrayList<>());
            }
            objectToCells.get(object).add(location);
        }
    }

    @Override
    public void remove(T object, ConvexShape shape) {
        ArrayList<Pair<Integer, Integer>> locations = objectToCells.get(object);
        for (Pair<Integer, Integer> location : locations) {
            ArrayList<T> objects = cells.get(location);
            if (objects != null) {
                objects.remove(object);
                if (objects.size() == 0) {
                    cells.remove(location);
                }
            }
        }
        objectToCells.remove(object);
    }

    @Override
    public void update(T object, ConvexShape oldShape, ConvexShape newShape) {
        remove(object, oldShape);
        insert(object, newShape);
    }

    @Override
    public ArrayList<T> findCloseObjects(ConvexShape shape) {
        ArrayList<Pair<Integer, Integer>> locations = getRelevantCellLocations(shape);
        HashSet<T> seen = new HashSet<>();
        ArrayList<T> result = new ArrayList<>();
        for (Pair<Integer, Integer> location : locations) {
            if (cells.containsKey(location)) {
                for (T obj : cells.get(location)) {
                    if (!seen.contains(obj)) {
                        seen.add(obj);
                        result.add(obj);
                    }
                }
            }
        }

        return result;
    }

    public void render(Graphics2D g, Color color) {
        g.setColor(color);
        for(Pair<Integer,Integer> pos : cells.keySet()) {
            g.drawRect(pos.getFirst() * (int)cellSize.getX(), pos.getSecond() * (int)cellSize.getY(), (int)cellSize.getX(), (int)cellSize.getY());
        }
    }
}
