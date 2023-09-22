package game;

import engine.GameObject;
import engine.PhysicsBehaviour;
import engine.SceneHierarchy;
import game.net.GameStatePacket;
import game.net.InputPacket;
import physics.PhysicsEngine;
import physics.Vec2D;
import physics.broad.SpatialGrid;
import physics.shape.RotatedTriangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents the game controller
 */
public class GameController {
    public static final int GAME_WIDTH = 2000;
    public static final int GAME_HEIGHT = 2000;
    private final PhysicsEngine engine;
    private final HashMap<Integer, VehicleObject> vehicles = new HashMap<>();
    private final HashMap<Integer, HashSet<GameInput>> playerInputs = new HashMap<>();
    private final ArrayList<BallObject> balls = new ArrayList<>();
    private final SceneHierarchy hierarchy = new SceneHierarchy();
    private final Random random = new Random();
    private GameObject[] map;
    private static int GAME_SPEED = 3;
    private static GameController singleton;
    public static GameController getInstance() {
        if (singleton == null) singleton = new GameController();
        return singleton;
    }

    private GameController() {
        //engine = new PhysicsEngine(new Vec2D(GAME_WIDTH+100, GAME_HEIGHT+100), new Vec2D(-50,-50));
        engine = new PhysicsEngine(new SpatialGrid<>(new Vec2D(50,50)));
    }

    public VehicleObject addVehicle(int id) {
        float randX = random.nextFloat() * (GAME_WIDTH-50) + 50;
        float randY = random.nextFloat() * (GAME_HEIGHT-50) + 50;
        Vec2D pos = new Vec2D(randX, randY);
        VehicleObject v = new VehicleObject(engine, pos, id);
        hierarchy.addObject(v);
        vehicles.put(id,v);
        return v;
    }

    public VehicleObject addVehicle(Vec2D pos, int id) {
        VehicleObject v = new VehicleObject(engine, pos, id);
        hierarchy.addObject(v);
        vehicles.put(id,v);
        return v;
    }

    public VehicleObject addVehicle(Vec2D pos, int id, Vec2D vel, float angle, boolean isDead) {
        VehicleObject v = new VehicleObject(engine, pos, id, angle, isDead);
        // set velocity
        PhysicsBehaviour behaviour = v.getBehaviour(PhysicsBehaviour.class);
        behaviour.setVelocity(vel);
        behaviour.setMass(1000);
        hierarchy.addObject(v);
        vehicles.put(id, v);
        return v;
    }

    public VehicleObject getVehicle(int id) {
        return vehicles.get(id);
    }

    public void removeVehicle(int id) {
        VehicleObject v = vehicles.get(id);
        hierarchy.removeObject(v);
        vehicles.remove(id);
    }

    public void unloadMap() {
        if (map == null) return;
        for (int i = 0; i < map.length; i++) {
            hierarchy.removeObject(map[i]);
        }
    }

    public void loadMap(String file) {
        try {
            GameMap map = MapLoader.getFromFile(file);
            if (map == null) return;
            GameObject[] objects = new GameObject[map.shapes.length];
            for (int i = 0; i < objects.length; i++) {
                GameObject go = new GameObject();
                PhysicsBehaviour behaviour = new PhysicsBehaviour(go, engine, map.shapes[i], true);
                go.addBehaviour(behaviour);
                objects[i] = go;
                if (GlobalRenderToggle.enableRenderer) {
                    GameControllerRenderer.addRenderer(go);
                }
            }
            for (GameObject go : objects) {
                hierarchy.addObject(go);
            }
            this.map = objects;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createBall(VehicleObject object, int id) {
        if (object.isDead()) return;
        PhysicsBehaviour playerBehaviour = object.getBehaviour(PhysicsBehaviour.class);
        Vec2D playerPos = playerBehaviour.getPosition();
        Vec2D dir = Vec2D.UP.rotated(((RotatedTriangle)playerBehaviour.getShape()).getAngle());
        Vec2D ballPos = playerPos.add(dir.mult(10));
        BallObject bo = new BallObject(engine, hierarchy.getRoot(), ballPos, dir.mult(350), id);
        bo.getBehaviour(PhysicsBehaviour.class).addToIgnore(playerBehaviour);
        balls.add(bo);
        hierarchy.addObject(bo);
    }

    public void removeBall(BallObject ball) {
        // hierarchy.removeObject(ball); // already called in ball's destroy method
        balls.remove(ball);
    }

    public void update() {
        engine.update(1/165f * GAME_SPEED);
        hierarchy.update();
    }

    /**
     *
     * @param inputPackets Mappings of player integer ID and their input packets (which lack IDs)
     */
    public void processInputs(HashMap<Integer, InputPacket> inputPackets) {
        for (Integer id : inputPackets.keySet()) {
            VehicleObject vehicle = vehicles.get(id);
            if (vehicle == null) continue;
            InputPacket input = inputPackets.get(id);
            GameInput[] inputs = input.getInput();
            boolean[] pressed = input.getPressed();
            boolean hasShoot = false;
            boolean hasReload = false;
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i] != GameInput.NONE && inputs[i] != GameInput.SHOOT) {
                    if (pressed[i]) {
                        if (!playerInputs.containsKey(id)) playerInputs.put(id, new HashSet<>());
                        playerInputs.get(id).add(inputs[i]);
                    } else {
                        if (playerInputs.containsKey(id)) {
                            playerInputs.get(id).remove(inputs[i]);
                        }
                    }
                }
                if (inputs[i] == GameInput.SHOOT) {
                    hasShoot = true;
                }
                if (inputs[i] == GameInput.RELOAD) {
                    hasReload = true;
                }
            }
            if (hasShoot) {
                createBall(vehicle, id);
            }
            if (hasReload) {
                // TODO: reload
            }
        }
    }

    public void processMovementInputs() {
        for (Integer id : playerInputs.keySet()) {
            VehicleObject vehicle = vehicles.get(id);
            if (vehicle == null) continue;
            HashSet<GameInput> inputs = playerInputs.get(id);
            if (vehicle.isDead()) continue;

            float forceAmount = 0.2f;
            float force = 0;
            float rotation = 0;
            if (inputs.contains(GameInput.FORWARD)) {
                force -= forceAmount;
            }
            if (inputs.contains(GameInput.BACKWARD)) {
                force += forceAmount;
            }
            if (inputs.contains(GameInput.LEFT)) {
                rotation -= 0.03f;
            }
            if (inputs.contains(GameInput.RIGHT)) {
                rotation += 0.03f;
            }
            vehicle.rotate(rotation);
            vehicle.accelerate(force);
        }
    }

    public void respawn(int id) {
        VehicleObject object = vehicles.get(id);
        if (object == null) return;
        if (!object.isDead()) return;
        object.setDead(false);
        float randX = random.nextFloat() * (GAME_WIDTH-50) + 50;
        float randY = random.nextFloat() * (GAME_HEIGHT-50) + 50;
        object.setPosition(new Vec2D(randX, randY));
        object.getBehaviour(PhysicsBehaviour.class).setStationary(false);
        object.getBehaviour(PhysicsBehaviour.class).setEnabled(true);
        GameLogger.getDefault().log("Respawn player " + id, GameLogger.Category.GAME);

    }

    public void onDeath(VehicleObject dead, int killer) {
        dead.setDead(true);
        dead.getBehaviour(PhysicsBehaviour.class).setStationary(true);
        dead.getBehaviour(PhysicsBehaviour.class).setEnabled(false);
        GameLogger.getDefault().log(killer + " killed player " + dead.getId(), GameLogger.Category.GAME);
    }

    public void updateFromPacket(GameStatePacket packet) {
        // delete all vehicles
        for (VehicleObject vehicle : vehicles.values()) {
            hierarchy.removeObject(vehicle);
        }
        vehicles.clear();
        // create new vehicles
        for (int i = 0; i < packet.vehicles.length; i++) {
            addVehicle(packet.vehicles[i].getPosition(), packet.vehicles[i].getId(), packet.vehicles[i].getVelocity(), packet.vehicles[i].getAngle(), packet.vehicles[i].isDead());
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
        engine.updateRemovals();
    }

    public GameStatePacket asGameStatePacket() {
        return new GameStatePacket(balls.toArray(new BallObject[0]),vehicles.values().toArray(new VehicleObject[0]));
    }

    SceneHierarchy getHierarchy() {
        return hierarchy;
    }

    PhysicsEngine getEngine() {
        return engine;
    }
}
