package util;

public class MathUtils {

    public static double clamp(double x, double lower, double upper) {
        return Math.max(lower, Math.min(x, upper));
    }

    public static int clamp(int x, int lower, int upper) {
        return Math.max(lower, Math.min(x, upper));
    }
}
