package game;

import engine.*;
import game.net.GameStatePacket;
import game.net.VehiclePacket;
import physics.*;
import physics.broad.SpatialGrid;
import physics.shape.AxisAlignedBoundingBox;
import physics.shape.RotatedTriangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the game controller
 */
public class GameController {
    public static int VEHICLE_COLLISION_MASK = 0b10;
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 400;
    private final PhysicsEngine engine;
    private final HashMap<Integer, VehicleObject> vehicles = new HashMap<>();
    private final ArrayList<BallObject> balls = new ArrayList<>();
    private final SceneHierarchy hierarchy = new SceneHierarchy();
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
    }

    public VehicleObject addVehicle(Vec2D pos, int id) {
        VehicleObject v = new VehicleObject(engine, pos, id);
        hierarchy.addObject(v);
        vehicles.put(id,v);
        return v;
    }

    public VehicleObject addVehicle(Vec2D pos, int id, Vec2D vel, float angle) {
        VehicleObject v = new VehicleObject(engine, pos, id, angle);
        // set velocity
        PhysicsBehaviour behaviour = v.getBehaviour(PhysicsBehaviour.class);
        behaviour.setVelocity(vel);
        hierarchy.addObject(v);
        vehicles.put(id, v);
        return v;
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
        AxisAlignedBoundingBox top = new AxisAlignedBoundingBox(new Vec2D(-100, -100), new Vec2D(GAME_WIDTH + 100, 20));
        AxisAlignedBoundingBox bot = new AxisAlignedBoundingBox(new Vec2D(-100, GAME_HEIGHT - 20), new Vec2D(GAME_WIDTH + 100, GAME_HEIGHT + 100));
        AxisAlignedBoundingBox left = new AxisAlignedBoundingBox(new Vec2D(-100, -100), new Vec2D(20, GAME_HEIGHT + 100));
        AxisAlignedBoundingBox right = new AxisAlignedBoundingBox(new Vec2D(GAME_WIDTH - 20, -100), new Vec2D(GAME_WIDTH + 100, GAME_HEIGHT + 100));
        hierarchy.addObject(createBoundingBox(top));
        hierarchy.addObject(createBoundingBox(bot));
        hierarchy.addObject(createBoundingBox(left));
        hierarchy.addObject(createBoundingBox(right));

    }

    public void createBall(VehicleObject object, int id) {
        PhysicsBehaviour playerBehaviour = object.getBehaviour(PhysicsBehaviour.class);
        Vec2D playerPos = playerBehaviour.getPosition();
        Vec2D dir = Vec2D.UP.rotated(((RotatedTriangle)playerBehaviour.getShape()).getAngle());
        Vec2D ballPos = playerPos.add(dir.mult(10));
        BallObject bo = new BallObject(engine, hierarchy.getRoot(), ballPos, dir.mult(350), id);
        balls.add(bo);
        hierarchy.addObject(bo);
    }

    public void render(Graphics2D g) {
        Time.deltaTime = (System.nanoTime() - Time.lastRenderNano) / 1000000000f;
        hierarchy.render(g);
        engine.getBroadphaseStructure().render(g, Color.BLACK);
        Time.lastRenderNano = System.nanoTime();
    }

    public void update() {
        engine.update(1/165f * GAME_SPEED);
        hierarchy.update();
    }

    public void updateFromPacket(GameStatePacket packet) {
        if (packet.vehicles.length != vehicles.size()) {
            // delete all vehicles
            for (VehicleObject vehicle : vehicles.values()) {
                hierarchy.removeObject(vehicle);
            }
            vehicles.clear();
            // create new vehicles
            for (int i = 0; i < packet.vehicles.length; i++) {
                addVehicle(packet.vehicles[i].getPosition(), packet.vehicles[i].getId(), packet.vehicles[i].getVelocity(), packet.vehicles[i].getAngle());
            }
        } else {
            for (VehiclePacket vehiclePacket : packet.vehicles) {
                VehicleObject vehicle = vehicles.get(vehiclePacket.getId());
                vehicle.setPosition(vehiclePacket.getPosition());
                vehicle.setAngle(vehiclePacket.getAngle());
                PhysicsBehaviour behaviour = vehicle.getBehaviour(PhysicsBehaviour.class);
                behaviour.setVelocity(vehiclePacket.getVelocity());
            }
        }

        // delete all balls
        for (BallObject ball : balls) {
            hierarchy.removeObject(ball);
        }
        balls.clear();
        // create new balls
        for (int i = 0; i < packet.balls.length; i++) {
            BallObject bo = new BallObject(engine, hierarchy.getRoot(), packet.balls[i].getPosition(), packet.balls[i].getVelocity(), packet.balls[i].getId(), packet.balls[i].getBounceCount());
            balls.add(bo);
            hierarchy.addObject(bo);
        }
    }

    public GameStatePacket asGameStatePacket() {
        return new GameStatePacket(balls.toArray(new BallObject[0]),vehicles.values().toArray(new VehicleObject[0]));
    }
}
