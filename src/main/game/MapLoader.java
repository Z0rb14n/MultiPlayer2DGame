package game;

import physics.Vec2D;
import physics.shape.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapLoader {
    public static GameMap getFromFile(String resource) {
        try (InputStream stream = MapLoader.class.getResourceAsStream(resource)) {
            if (stream == null) return null;
            try (BufferedReader buf = new BufferedReader(new InputStreamReader(stream))) {
                GameMap gameMap = new GameMap();
                ArrayList<ConvexShape> shapes = new ArrayList<>();
                String str;
                boolean hasParsedHeader = false;
                while ((str = buf.readLine()) != null) {
                    if (blankOrComment(str)) continue;
                    if (!hasParsedHeader) {
                        String[] split = str.split(" ");
                        if (split.length < 3) {
                            throw new RuntimeException("Invalid map header: " + str);
                        }
                        gameMap.width = Integer.parseUnsignedInt(split[split.length-2]);
                        gameMap.height = Integer.parseUnsignedInt(split[split.length-1]);
                        gameMap.name = str.substring(0, split[split.length-2].length() + split[split.length-1].length() + 1);
                        hasParsedHeader = true;
                    } else {
                        shapes.add(parseShape(str.trim()));
                    }
                }
                gameMap.shapes = shapes.toArray(new ConvexShape[0]);
                return gameMap;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static ConvexShape parseShape(String line) {
        String lower = line.toLowerCase();
        String[] split = lower.split(" ");
        if (lower.startsWith("r")) {
            if (split.length == 5) {
                return new AxisAlignedBoundingBox(new Vec2D(Integer.parseInt(split[1]),Integer.parseInt(split[2])),
                        new Vec2D(Integer.parseInt(split[3]),Integer.parseInt(split[4])));
            } else if (split.length == 6) {
                return new RotatedRectangle(new Vec2D(Integer.parseInt(split[1]),Integer.parseInt(split[2])),
                        Integer.parseInt(split[3]),Integer.parseInt(split[4]), (float) Math.toRadians(Integer.parseInt(split[5])));
            } else {
                throw new IllegalArgumentException("Needs 4-5 entries: " + line);
            }
        } else if (lower.startsWith("c")) {
            if (split.length == 4) {
                return new Circle(new Vec2D(Integer.parseInt(split[1]), Integer.parseInt(split[2])),
                        Integer.parseInt(split[3]));
            } else {
                throw new IllegalArgumentException("Needs 3 entries: " + line);
            }
        } else if (lower.startsWith("t")) {
            if (split.length == 7) {
                return new Triangle(new Vec2D(Integer.parseInt(split[1]), Integer.parseInt(split[2])),
                        new Vec2D(Integer.parseInt(split[3]), Integer.parseInt(split[4])),
                        new Vec2D(Integer.parseInt(split[5]), Integer.parseInt(split[6])));
            } else {
                throw new IllegalArgumentException("Needs 6 entries: " + line);
            }
        } else if (lower.startsWith("p")) {
            if (split.length < 7 || (split.length % 2) != 1) {
                throw new IllegalArgumentException("Needs 6+ even entries: " + line);
            }
            Vec2D[] vecs = new Vec2D[(split.length-1)/2];
            for (int i = 1; i < split.length; i += 2) {
                vecs[(i-1)/2] = new Vec2D(Integer.parseInt(split[i]), Integer.parseInt(split[i+1]));
            }
            return new ConvexPolygon(vecs);
        } else {
            throw new IllegalArgumentException("Invalid start: " + line);
        }
    }

    private static boolean blankOrComment(String line) {
        return line == null || line.trim().isEmpty() || line.trim().startsWith("//");
    }
}
