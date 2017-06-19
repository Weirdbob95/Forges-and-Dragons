package chunk;

import static chunk.Chunk.SIDE_LENGTH_2;
import org.joml.Vector3i;

public class BlockArray {

    private final int[] array = new int[SIDE_LENGTH_2 * SIDE_LENGTH_2 * SIDE_LENGTH_2];

    public int getColor(int x, int y, int z) {
        return array[(x + 1) * SIDE_LENGTH_2 * SIDE_LENGTH_2 + (y + 1) * SIDE_LENGTH_2 + (z + 1)];
    }

    public int getColor(Vector3i pos) {
        return getColor(pos.x, pos.y, pos.z);
    }

    public float[] getColorArray(int x, int y, int z) {
        int col = getColor(x, y, z);
        float[] r = new float[3];
        for (int j = 2; j >= 0; j--) {
            r[j] = (col % 256) / 255.0f;
            col /= 256;
        }
        return r;
    }

    public float[] getColorArray(Vector3i pos) {
        return getColorArray(pos.x, pos.y, pos.z);
    }

    public boolean isSolid(int x, int y, int z) {
        return getColor(x, y, z) != 0;
    }

    public boolean isSolid(Vector3i pos) {
        return getColor(pos) != 0;
    }

    public void setColor(int x, int y, int z, int color) {
        array[(x + 1) * SIDE_LENGTH_2 * SIDE_LENGTH_2 + (y + 1) * SIDE_LENGTH_2 + (z + 1)] = color;
    }

    public boolean willDraw(int x, int y, int z) {
        return isSolid(x, y, z) != isSolid(x - 1, y, z) || isSolid(x, y, z) != isSolid(x + 1, y, z)
                || isSolid(x, y, z) != isSolid(x, y - 1, z) || isSolid(x, y, z) != isSolid(x, y + 1, z)
                || isSolid(x, y, z) != isSolid(x, y, z - 1) || isSolid(x, y, z) != isSolid(x, y, z + 1);
    }
}
