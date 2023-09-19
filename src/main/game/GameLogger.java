package game;

import java.util.HashMap;

import static game.GameLogger.Category.*;

public class GameLogger {
    private static GameLogger defaultInstance;

    public static GameLogger getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new GameLogger();
        }
        return defaultInstance;
    }

    public final HashMap<Category, Level> typeLevels = new HashMap<>();
    {
        typeLevels.put(PHYSICS, Level.WARNING);
        typeLevels.put(VEHICLE_DEBUG, Level.IGNORE);
        typeLevels.put(VEC2D_DEBUG, Level.IGNORE);
        typeLevels.put(PERFORMANCE, Level.IGNORE);
        typeLevels.put(COLLISION, Level.WARNING);
        typeLevels.put(INPUT, Level.WARNING);
        typeLevels.put(RENDER, Level.WARNING);
        typeLevels.put(GAME, Level.WARNING);
        typeLevels.put(NETWORK, Level.IGNORE);
        typeLevels.put(AUDIO, Level.WARNING);
        typeLevels.put(UI, Level.WARNING);
        typeLevels.put(DEBUG, Level.INFO);
    }

    private Level defaultLevel = Level.INFO;

    public GameLogger() {
    }

    public void log(String str, Level level) {
        switch (level) {
            case IGNORE:
                return;
            case ERROR:
                System.err.println(str);
                return;
            case WARNING:
                System.out.println("\u001B[33m" + str + "\u001B[0m");
                return;
            default:
                System.out.println(str);
        }
    }

    public void log(String str) {
        log("[DEFAULT]" + str, defaultLevel);
    }

    public void log(String str, Category category) {
        if (typeLevels.containsKey(category)) log("[" + category + "]" + str, typeLevels.get(category));
        else log("[" + category + "]" + str, defaultLevel);
    }

    public enum Level {
        INFO,
        WARNING,
        ERROR,
        IGNORE
    }

    public enum Category {
        PHYSICS, VEHICLE_DEBUG, VEC2D_DEBUG, PERFORMANCE, COLLISION, INPUT, RENDER, GAME, NETWORK, AUDIO, UI, DEBUG
    }
}
