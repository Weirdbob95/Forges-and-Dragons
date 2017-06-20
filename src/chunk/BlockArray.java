package chunk;

import static chunk.Chunk.SIDE_LENGTH_2;

public class BlockArray implements BlockStorage {

    private final int size;
    private final int[] array;

    public BlockArray() {
        this(SIDE_LENGTH_2);
    }

    public BlockArray(int size) {
        this.size = size;
        array = new int[size * size * size];
    }

    public BlockArray downsample2() {
        int newSize = size / 2 + 1;
        BlockArray r = new BlockArray(newSize);
        for (int x = 0; x < newSize; x++) {
            for (int y = 0; y < newSize; y++) {
                for (int z = 0; z < newSize; z++) {
                    r.set(x - 1, y - 1, z - 1, get(2 * x - 1, 2 * y - 1, 2 * z - 1));
                }
            }
        }
        return r;
    }

    @Override
    public int get(int x, int y, int z) {
        return array[(x + 1) * size * size + (y + 1) * size + (z + 1)];
    }

    public void set(int x, int y, int z, int color) {
        array[(x + 1) * size * size + (y + 1) * size + (z + 1)] = color;
    }

    public boolean willDraw(int x, int y, int z) {
        return solid(x, y, z) != solid(x - 1, y, z) || solid(x, y, z) != solid(x + 1, y, z)
                || solid(x, y, z) != solid(x, y - 1, z) || solid(x, y, z) != solid(x, y + 1, z)
                || solid(x, y, z) != solid(x, y, z - 1) || solid(x, y, z) != solid(x, y, z + 1);
    }
}
