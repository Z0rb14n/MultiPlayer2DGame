package game;

import physics.PhysicsEngine;
import physics.PhysicsObject;
import physics.Vec2D;
import physics.shape.AxisAlignedBoundingBox;
import physics.vis.QuadTreeRender;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents the game controller
 */
public class GameController {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 400;
    private final PhysicsEngine engine;
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Ball> balls = new ArrayList<>();
    private final Vehicle player;
    private AxisAlignedBoundingBox top;
    private AxisAlignedBoundingBox bot;
    private AxisAlignedBoundingBox left;
    private AxisAlignedBoundingBox right;
    private static int GAME_SPEED = 3;
    private static GameController singleton;
    public static GameController getInstance() {
        if (singleton == null) singleton = new GameController();
        return singleton;
    }

    private GameController() {
        engine = new PhysicsEngine(new Vec2D(GAME_WIDTH+100, GAME_HEIGHT+100), new Vec2D(-50,-50));
        createBoundingBoxes();
        // create test vehicle
        Vehicle v = new Vehicle(new Vec2D(100,100));
        vehicles.add(v);
        player = v;
        engine.add(v.getPhysicsObject());
    }

    private void createBoundingBoxes() {
        top = new AxisAlignedBoundingBox(new Vec2D(-500,-500), new Vec2D(GAME_WIDTH+500,20));
        bot = new AxisAlignedBoundingBox(new Vec2D(-500,GAME_HEIGHT-20), new Vec2D(GAME_WIDTH+500,GAME_HEIGHT+500));
        left = new AxisAlignedBoundingBox(new Vec2D(-500,-500), new Vec2D(20,GAME_HEIGHT+500));
        right = new AxisAlignedBoundingBox(new Vec2D(GAME_WIDTH-20,-500), new Vec2D(GAME_WIDTH+500,GAME_HEIGHT+500));
        PhysicsObject topObj = new PhysicsObject(top, Vec2D.ZERO, true);
        engine.add(topObj);
        PhysicsObject botObj = new PhysicsObject(bot, Vec2D.ZERO, true);
        engine.add(botObj);
        PhysicsObject leftObj = new PhysicsObject(left, Vec2D.ZERO, true);
        engine.add(leftObj);
        PhysicsObject rightObj = new PhysicsObject(right, Vec2D.ZERO, true);
        engine.add(rightObj);
    }

    public void forceRemoveBall(Ball ball) {
        engine.removeImmediateOOB(ball.getPhysicsObject());
        balls.remove(ball);
    }

    public void removeBall(Ball ball) {
        engine.remove(ball.getPhysicsObject());
        balls.remove(ball);
    }

    public void createBall() {
        Vec2D playerPos = player.getPhysicsObject().getPosition();
        Vec2D playerVel = player.getPhysicsObject().getVelocity();
        Vec2D ballPos = playerPos.add(playerVel.normalize().scaleTo(10));
        Ball ball = new Ball(ballPos);
        balls.add(ball);
        ball.getPhysicsObject().setVelocity(new Vec2D(100,100));
        engine.add(ball.getPhysicsObject());
    }

    public Vehicle getPlayer() {
        return player;
    }

    public void render(Graphics2D g) {
        renderBounds(g);
        for (Vehicle v : vehicles) {
            v.render(g);
        }
        for (Ball b : balls) {
            b.render(g);
        }
        QuadTreeRender.drawTree(g, engine.getTree());
    }

    private void renderBounds(Graphics2D g) {
        g.setColor(Color.BLACK);
        // use top, left, bot, right
        g.fillRect((int)top.getBottomLeft().getX(), (int)top.getBottomLeft().getY(), (int)top.getWidth(), (int)top.getHeight());
        g.fillRect((int)bot.getBottomLeft().getX(), (int)bot.getBottomLeft().getY(), (int)bot.getWidth(), (int)bot.getHeight());
        g.fillRect((int)left.getBottomLeft().getX(), (int)left.getBottomLeft().getY(), (int)left.getWidth(), (int)left.getHeight());
        g.fillRect((int)right.getBottomLeft().getX(), (int)right.getBottomLeft().getY(), (int)right.getWidth(), (int)right.getHeight());


    }

    public void update() {
        engine.update(1/60f * GAME_SPEED);
    }
}
