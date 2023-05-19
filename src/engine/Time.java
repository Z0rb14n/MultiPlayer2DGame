package engine;

public class Time {
    public static long lastRenderNano = System.nanoTime();
    public static float deltaTime;

    public static float physicsDeltaTime = 1f / 165f;
}
