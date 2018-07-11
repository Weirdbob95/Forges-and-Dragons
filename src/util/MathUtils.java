package util;

import org.joml.Vector2d;

public class MathUtils {

    public static double clamp(double x, double lower, double upper) {
        return Math.max(lower, Math.min(x, upper));
    }

    public static int clamp(int x, int lower, int upper) {
        return Math.max(lower, Math.min(x, upper));
    }

    public static double direction(Vector2d v) {
        return Math.atan2(v.y, v.x);
    }
}
