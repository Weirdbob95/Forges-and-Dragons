package chunk;

import static chunk.Chunk.SIDE_LENGTH;

public class OctTree {

    public int value = 0xFFFFFF;
    public OctTree[] children;
    public int size;

    public OctTree(BlockArray v) {
        this(v, 0, 0, 0, SIDE_LENGTH);
    }

    private OctTree(BlockArray v, int i, int j, int k, int size) {
        this.size = size;
        boolean willDraw = false;
        for (int x = 0; x < size && !willDraw; x++) {
            for (int y = 0; y < size && !willDraw; y++) {
                for (int z = 0; z < size && !willDraw; z++) {
                    willDraw = v.willDraw(i * size + x, j * size + y, k * size + z);
                }
            }
        }
        if (!willDraw || size == 1) {
            value = v.getColor(i * size, j * size, k * size);
        } else {
            children = new OctTree[8];
            children[0] = new OctTree(v, i * 2, j * 2, k * 2, size / 2);
            children[1] = new OctTree(v, i * 2 + 1, j * 2, k * 2, size / 2);
            children[2] = new OctTree(v, i * 2, j * 2 + 1, k * 2, size / 2);
            children[3] = new OctTree(v, i * 2 + 1, j * 2 + 1, k * 2, size / 2);
            children[4] = new OctTree(v, i * 2, j * 2, k * 2 + 1, size / 2);
            children[5] = new OctTree(v, i * 2 + 1, j * 2, k * 2 + 1, size / 2);
            children[6] = new OctTree(v, i * 2, j * 2 + 1, k * 2 + 1, size / 2);
            children[7] = new OctTree(v, i * 2 + 1, j * 2 + 1, k * 2 + 1, size / 2);
        }
    }

    public int get(int x, int y, int z) {
        if (children == null) {
            return value;
        }
        int child = 0;
        if (x >= size / 2) {
            child += 1;
        }
        if (y >= size / 2) {
            child += 2;
        }
        if (z >= size / 2) {
            child += 4;
        }
        return children[child].get(x % (size / 2), y % (size / 2), z % (size / 2));
    }
}
