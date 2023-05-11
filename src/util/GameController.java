package util;

import physics.PhysicsEngine;
import physics.PhysicsObject;
import physics.Vec2D;
import physics.shape.AxisAlignedBoundingBox;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Stack;

/**
 * Represents the game controller
 */
public class GameController {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 400;
    private PhysicsEngine engine;
    private static int GAME_SPEED = 3;
    private static GameController singleton;
    //<editor-fold desc="Static Methods">
    public static GameController getInstance() {
        if (singleton == null) singleton = new GameController();
        return singleton;
    }
    static int gameRightBound() {
        return GAME_WIDTH;
    }
    static int gameBottomBound() {
        return GAME_HEIGHT;
    }
    static int gameTopBound() {
        return 0;
    }
    static int gameLeftBound() {
        return 0;
    }

    //</editor-fold>

    private GameController() {
        engine = new PhysicsEngine(new Vec2D(GAME_WIDTH+100, GAME_HEIGHT+100));
        createBoundingBoxes();
    }

    private void createBoundingBoxes() {
        AxisAlignedBoundingBox top = new AxisAlignedBoundingBox(new Vec2D(-500,-500), new Vec2D(GAME_WIDTH+500,20));
        AxisAlignedBoundingBox bot = new AxisAlignedBoundingBox(new Vec2D(-500,GAME_HEIGHT-20), new Vec2D(GAME_WIDTH+500,GAME_HEIGHT+500));
        AxisAlignedBoundingBox left = new AxisAlignedBoundingBox(new Vec2D(-500,-500), new Vec2D(20,GAME_HEIGHT+500));
        AxisAlignedBoundingBox right = new AxisAlignedBoundingBox(new Vec2D(GAME_WIDTH-20,-500), new Vec2D(GAME_WIDTH+500,GAME_HEIGHT+500));
        PhysicsObject topObj = new PhysicsObject(top, Vec2D.ZERO, true);
        engine.add(topObj);
        PhysicsObject botObj = new PhysicsObject(bot, Vec2D.ZERO, true);
        engine.add(botObj);
        PhysicsObject leftObj = new PhysicsObject(left, Vec2D.ZERO, true);
        engine.add(leftObj);
        PhysicsObject rightObj = new PhysicsObject(right, Vec2D.ZERO, true);
        engine.add(rightObj);
    }

    public void render(Graphics2D g) {
        renderBounds(g);
    }

    private void renderBounds(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(20,20,GAME_WIDTH-40,GAME_HEIGHT-40);
    }

    public void update() {
        engine.update(1/60f * GAME_SPEED);
    }

    public void reset() {
    }
}
