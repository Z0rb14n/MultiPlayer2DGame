package game;

import engine.*;
import physics.*;
import physics.broad.SpatialGrid;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.Triangle;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents the game controller
 */
public class GameController {
    public static int VEHICLE_COLLISION_MASK = 0b10;
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 400;
    private final PhysicsEngine engine;
    private final ArrayList<GameObject> vehicles = new ArrayList<>();
    private final ArrayList<BallObject> balls = new ArrayList<>();
    private final SceneHierarchy hierarchy = new SceneHierarchy();
    private final GameObject player;
    private static int GAME_SPEED = 3;
    private static GameController singleton;
    public static GameController getInstance() {
        if (singleton == null) singleton = new GameController();
        return singleton;
    }

    private GameController() {
        //engine = new PhysicsEngine(new Vec2D(GAME_WIDTH+100, GAME_HEIGHT+100), new Vec2D(-50,-50));
        engine = new PhysicsEngine(new SpatialGrid<>(new Vec2D(50,50)));
        createBoundingBoxes();
        // create test vehicle
        GameObject v = createVehicle(new Vec2D(100,100));
        hierarchy.addObject(v);
        vehicles.add(v);
        player = v;
    }

    private GameObject createVehicle(Vec2D position) {
        Triangle triangle = new Triangle(new Vec2D(-10,0), new Vec2D(10,0), new Vec2D(0,20));
        GameObject gameObject = new GameObject(position);
        PhysicsBehaviour behaviour = new PhysicsBehaviour(gameObject,engine,triangle,false);
        behaviour.setCollisionMask(VEHICLE_COLLISION_MASK);
        gameObject.addBehaviour(behaviour);
        TriangleRenderer renderer = new TriangleRenderer(gameObject, Color.RED, true);
        gameObject.addBehaviour(renderer);
        return gameObject;
    }

    private GameObject createBoundingBox(AxisAlignedBoundingBox box) {
        GameObject gameObject = new GameObject();
        PhysicsBehaviour behaviour = new PhysicsBehaviour(gameObject,engine,box,true);
        gameObject.addBehaviour(behaviour);
        BoxRenderer renderer = new BoxRenderer(gameObject, Color.BLUE, true);
        gameObject.addBehaviour(renderer);
        return gameObject;
    }

    private void createBoundingBoxes() {
        AxisAlignedBoundingBox top = new AxisAlignedBoundingBox(new Vec2D(-500, -500), new Vec2D(GAME_WIDTH + 500, 20));
        AxisAlignedBoundingBox bot = new AxisAlignedBoundingBox(new Vec2D(-500, GAME_HEIGHT - 20), new Vec2D(GAME_WIDTH + 500, GAME_HEIGHT + 500));
        AxisAlignedBoundingBox left = new AxisAlignedBoundingBox(new Vec2D(-500, -500), new Vec2D(20, GAME_HEIGHT + 500));
        AxisAlignedBoundingBox right = new AxisAlignedBoundingBox(new Vec2D(GAME_WIDTH - 20, -500), new Vec2D(GAME_WIDTH + 500, GAME_HEIGHT + 500));
        hierarchy.addObject(createBoundingBox(top));
        hierarchy.addObject(createBoundingBox(bot));
        hierarchy.addObject(createBoundingBox(left));
        hierarchy.addObject(createBoundingBox(right));

    }

    public void createBall() {
        Vec2D playerPos = player.getBehaviour(PhysicsBehaviour.class).getPosition();
        Vec2D playerVel = player.getBehaviour(PhysicsBehaviour.class).getVelocity();
        Vec2D ballPos = playerPos.add(playerVel.normalize().scaleTo(10));
        BallObject bo = new BallObject(engine, hierarchy.getRoot(), ballPos, new Vec2D(100,100));
        balls.add(bo);
        hierarchy.addObject(bo);
    }

    public GameObject getPlayer() {
        return player;
    }

    public void render(Graphics2D g) {
        Time.deltaTime = (System.nanoTime() - Time.lastRenderNano) / 1000000000f;
        hierarchy.update();
        hierarchy.render(g);
        engine.getBroadphaseStructure().render(g, Color.BLACK);
        Time.lastRenderNano = System.nanoTime();
    }

    public void update() {
        engine.update(1/165f * GAME_SPEED);
    }
}
