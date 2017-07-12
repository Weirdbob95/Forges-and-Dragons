package util;

import java.util.Arrays;
import java.util.List;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class MathUtils {

    public static final List<Vector3i> ALL_DIRS = Arrays.asList(
            new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0),
            new Vector3i(0, -1, 0), new Vector3i(0, 1, 0),
            new Vector3i(0, 0, -1), new Vector3i(0, 0, 1));

    public static final List<Vector2i> ALL_DIRS_2 = Arrays.asList(
            new Vector2i(-1, 0), new Vector2i(1, 0),
            new Vector2i(0, -1), new Vector2i(0, 1));

    public static double clamp(double x, double lower, double upper) {
        return Math.max(lower, Math.min(x, upper));
    }

    public static int clamp(int x, int lower, int upper) {
        return Math.max(lower, Math.min(x, upper));
    }

    public static int dirPos(Vector3i dir) {
        return Math.max(dirPosNeg(dir), 0);
    }

    public static int dirPosNeg(Vector3i dir) {
        return dir.x + dir.y + dir.z;
    }

    public static int getComponent(Vector3i v, Vector3i dir, int dim) {
        if (dir.x != 0) {
            switch (dim) {
                case 0:
                    return v.y;
                case 1:
                    return v.z;
                case 2:
                    return v.x;
            }
        } else if (dir.y != 0) {
            switch (dim) {
                case 0:
                    return v.x;
                case 1:
                    return v.z;
                case 2:
                    return v.y;
            }
        } else {
            switch (dim) {
                case 0:
                    return v.x;
                case 1:
                    return v.y;
                case 2:
                    return v.z;
            }
        }
        throw new RuntimeException("Invalid input");
    }

    public static Vector3i orderComponents(int v, Vector3i dir, int i, int j) {
        if (dir.x != 0) {
            return new Vector3i(v, i, j);
        } else if (dir.y != 0) {
            return new Vector3i(i, v, j);
        } else {
            return new Vector3i(i, j, v);
        }
    }

    public static Vector3d toVec3d(Vector2i v) {
        return new Vector3d(v.x, v.y, 0);
    }

    public static Vector3d toVec3d(Vector3i v) {
        return new Vector3d(v.x, v.y, v.z);
    }
}
