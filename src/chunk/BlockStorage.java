package chunk;

import org.joml.Vector3i;

public interface BlockStorage {

    public int get(int x, int y, int z);

    public default int get(Vector3i pos) {
        return get(pos.x, pos.y, pos.z);
    }

    public default float[] getColorArray(int x, int y, int z) {
        int col = get(x, y, z);
        float[] r = new float[3];
        for (int j = 2; j >= 0; j--) {
            r[j] = (col % 256) / 255.0f;
            col /= 256;
        }
        return r;
    }

    public default float[] getColorArray(Vector3i pos) {
        return getColorArray(pos.x, pos.y, pos.z);
    }

    public default boolean solid(int x, int y, int z) {
        return get(x, y, z) != 0;
    }

    public default boolean solid(Vector3i pos) {
        return solid(pos.x, pos.y, pos.z);
    }
}
