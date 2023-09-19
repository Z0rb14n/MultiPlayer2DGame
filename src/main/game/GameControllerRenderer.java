package game;

import engine.Time;
import physics.Vec2D;

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
}
