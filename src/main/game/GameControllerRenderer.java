package game;

import engine.BoxRenderer;
import engine.GameObject;
import engine.Time;

import java.awt.*;

public class GameControllerRenderer {
    private static boolean didAddBBRenders = false;
    public static void render(Graphics2D g) {
        Time.deltaTime = (System.nanoTime() - Time.lastRenderNano) / 1000000000f;

        if (!didAddBBRenders) {
            for (GameObject gameObject : GameController.getInstance().getBoundingBoxes()) {
                BoxRenderer renderer = new BoxRenderer(gameObject, Color.BLUE, true);
                gameObject.addBehaviour(renderer);
            }
            didAddBBRenders = true;
        }
        GameController.getInstance().getHierarchy().render(g);
        GameController.getInstance().getEngine().getBroadphaseStructure().render(g, Color.BLACK);
        Time.lastRenderNano = System.nanoTime();
    }
}
