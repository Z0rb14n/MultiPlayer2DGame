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
                Pair<Integer,Integer> pos = new Pair<>(x, y);
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
                Pair<Integer,Integer> pos = new Pair<>(x,y);
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
                Pair<Integer,Integer> pos = new Pair<>(x,y);
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
        for(Pair<Integer,Integer> pos : cells.keySet()) {
            g.drawRect(pos.getFirst() * (int)cellSize.getX(), pos.getSecond() * (int)cellSize.getY(), (int)cellSize.getX(), (int)cellSize.getY());
        }
    }
}
