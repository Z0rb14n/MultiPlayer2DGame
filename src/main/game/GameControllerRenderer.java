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
        if (centerVehicle != -1) {
            VehicleObject object = GameController.getInstance().getVehicle(centerVehicle);
            if (object == null) {
                System.out.println("UHHHHHH");
            } else {
                float screenWidth = 800;
                float screenHeight = 600;
                Vec2D position = object.getPosition();
                float xMin = 50 + screenWidth/2;
                float xMax = GameController.GAME_WIDTH-(screenWidth/2) + 50;
                float yMin = screenHeight/2 + 50;
                float yMax = GameController.GAME_HEIGHT-(screenHeight/2) + 50;
                Vec2D clamped = position.clamp(xMin, xMax, yMin, yMax);
                Vec2D min = new Vec2D(xMin, yMin);
                Vec2D diff = min.sub(clamped);
                g.translate(diff.getX(), diff.getY());
            }
        }
        GameController.getInstance().getHierarchy().render(g);
        GameController.getInstance().getEngine().getBroadphaseStructure().render(g, Color.BLACK);
        Time.lastRenderNano = System.nanoTime();
    }
}
