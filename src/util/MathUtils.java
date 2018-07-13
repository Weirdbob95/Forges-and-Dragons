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

    public static double mod(double x, double m) {
        return (x % m + m) % m;
    }

    public static Vector2d rotate(Vector2d v, double angle) {
        return new Vector2d(Math.cos(angle) * v.x - Math.sin(angle) * v.y,
                Math.sin(angle) * v.x + Math.cos(angle) * v.y);
    }

    public static Vector2d unitVector(double angle) {
        return rotate(new Vector2d(1, 0), angle);
    }
}
