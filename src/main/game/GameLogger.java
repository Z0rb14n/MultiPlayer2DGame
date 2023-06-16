package game;

import java.util.HashMap;

public class GameLogger {
    private static GameLogger defaultInstance;

    public static GameLogger getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new GameLogger();
        }
        return defaultInstance;
    }

    private final HashMap<String, Level> typeLevels = new HashMap<>();

    {
        typeLevels.put("PHYSICS", Level.WARNING);
        typeLevels.put("VEHICLE_DEBUG", Level.IGNORE);
        typeLevels.put("VEC2D_DEBUG", Level.IGNORE);
        typeLevels.put("PERFORMANCE", Level.IGNORE);
        typeLevels.put("COLLISION", Level.WARNING);
        typeLevels.put("INPUT", Level.WARNING);
        typeLevels.put("RENDER", Level.WARNING);
        typeLevels.put("GAME", Level.WARNING);
        typeLevels.put("NETWORK", Level.IGNORE);
        typeLevels.put("AUDIO", Level.WARNING);
        typeLevels.put("UI", Level.WARNING);
        typeLevels.put("DEBUG", Level.INFO);
        typeLevels.put("IGNORE", Level.IGNORE);
    }

    private Level defaultLevel = Level.INFO;

    public GameLogger() {
    }

    public void setLevel(String tag, Level level) {
        typeLevels.put(tag, level);
    }

    private void log(String str, Level level) {
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

    public void log(String str, String tag) {
        if (typeLevels.containsKey(tag)) log("[" + tag + "]" + str, typeLevels.get(tag));
        else log("[" + tag + "]" + str, defaultLevel);
    }

    public enum Level {
        INFO,
        WARNING,
        ERROR,
        IGNORE
    }
}
