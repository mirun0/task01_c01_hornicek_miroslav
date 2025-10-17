package utils;

public class MathUtils {
    public static float length(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
