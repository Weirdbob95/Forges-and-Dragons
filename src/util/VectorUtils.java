package util;

import java.util.Arrays;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class VectorUtils {

    public static final List<Vector3i> ALL_DIRS = Arrays.asList(
            new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0),
            new Vector3i(0, -1, 0), new Vector3i(0, 1, 0),
            new Vector3i(0, 0, -1), new Vector3i(0, 0, 1));

    public static int dirPos(Vector3i dir) {
        return Math.max(dirPosNeg(dir), 0);
    }

    public static int dirPosNeg(Vector3i dir) {
        return dir.x + dir.y + dir.z;
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

    public static Vector3d toVec3d(Vector3i v) {
        return new Vector3d(v.x, v.y, v.z);
    }
}
