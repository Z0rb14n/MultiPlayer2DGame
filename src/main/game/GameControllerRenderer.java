package game;

import engine.*;
import physics.Vec2D;
import physics.shape.*;

import java.awt.*;

public class GameControllerRenderer {
    public static void render(Graphics2D g) {
        render(g, -1);
    }
    public static void render(Graphics2D g, int centerVehicle) {
        Time.deltaTime = (System.nanoTime() - Time.lastRenderNano) / 1000000000f;
        float screenWidth = g.getClipBounds().width;
        float screenHeight = g.getClipBounds().height;
        Vec2D diff = Vec2D.ZERO;
        if (centerVehicle != -1) {
            VehicleObject object = GameController.getInstance().getVehicle(centerVehicle);
            if (object == null) {
                GameLogger.getDefault().log("Can't locate this vehicle to render!", GameLogger.Category.UI);
            } else {
                Vec2D position = object.getPosition();
                float xMin = 50 + screenWidth/2;
                float xMax = GameController.GAME_WIDTH-(screenWidth/2) + 50;
                float yMin = screenHeight/2 + 50;
                float yMax = GameController.GAME_HEIGHT-(screenHeight/2) + 50;
                Vec2D clamped = position.clamp(xMin, xMax, yMin, yMax);
                Vec2D min = new Vec2D(xMin, yMin);
                diff = min.sub(clamped);
                g.translate(diff.getX(), diff.getY());
            }
        }
        GameController.getInstance().getHierarchy().render(g);
        GameController.getInstance().getEngine().getBroadphaseStructure().render(g, Color.BLACK);
        if (centerVehicle != -1) {
            VehicleObject object = GameController.getInstance().getVehicle(centerVehicle);
            if (object != null && object.isDead()) {
                g.translate(-diff.getX(), -diff.getY());
                g.drawString("You died! Press R to become alive.", screenWidth/2,screenHeight/2);
            }
        }
        Time.lastRenderNano = System.nanoTime();
    }

    public static void addRenderer(GameObject gameObject) {
        PhysicsBehaviour behaviour = gameObject.getBehaviour(PhysicsBehaviour.class);
        if (behaviour == null) return;
        if (behaviour.getShape() instanceof AxisAlignedBoundingBox) {
            BoxRenderer renderer = new BoxRenderer(gameObject, Color.BLUE, true);
            gameObject.addBehaviour(renderer);
        } else if (behaviour.getShape() instanceof Circle) {
            CircleRenderer renderer = new CircleRenderer(gameObject, Color.BLUE, true);
            gameObject.addBehaviour(renderer);
        } else if (behaviour.getShape() instanceof Triangle) {
            TriangleRenderer renderer = new TriangleRenderer(gameObject, Color.BLUE, true);
            gameObject.addBehaviour(renderer);
        } else if (behaviour.getShape() instanceof RotatedRectangle) {
            RotatedRectangleRenderer renderer = new RotatedRectangleRenderer(gameObject, Color.BLUE, true);
            gameObject.addBehaviour(renderer);
        } else if (behaviour.getShape() instanceof ConvexPolygon) {
            ConvexPolygonRenderer renderer = new ConvexPolygonRenderer(gameObject, Color.BLUE, true);
            gameObject.addBehaviour(renderer);
        }
    }
}
