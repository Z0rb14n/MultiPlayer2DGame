package util;

public class GameLogger {
    private static GameLogger singleton;
    public static GameLogger getInstance() {
        if (singleton == null) singleton = new GameLogger();
        return singleton;
    }
    public static void addMessage(String msg) {
        System.out.println(msg);
    }
}
