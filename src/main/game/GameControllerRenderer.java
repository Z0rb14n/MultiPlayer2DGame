package game;

import engine.Time;

import java.awt.*;

public class GameControllerRenderer {
    public static void render(Graphics2D g) {
        Time.deltaTime = (System.nanoTime() - Time.lastRenderNano) / 1000000000f;
        GameController.getInstance().getHierarchy().render(g);
        GameController.getInstance().getEngine().getBroadphaseStructure().render(g, Color.BLACK);
        Time.lastRenderNano = System.nanoTime();
    }
}
