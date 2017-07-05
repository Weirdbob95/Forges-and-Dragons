package chunk;

import static chunk.Chunk.SIDE_LENGTH;
import java.util.stream.Stream;
import org.joml.Vector3d;

public class OctTree implements BlockStorage {

    public int value = 0xFFFFFF;
    public double density;
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
            value = v.get(i * size, j * size, k * size);
            density = value == 0 ? 0 : 1;
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
//            value = Stream.of(children).mapToInt(o -> o.value).filter(x -> x != 0).findFirst().getAsInt();
            density = Stream.of(children).mapToDouble(o -> o.density).average().getAsDouble();
        }
    }

    public boolean collides(Vector3d min, Vector3d max) {
        if (density == 0) {
            return false;
        }
        if (density == 1) {
            return true;
        }
        for (int x = Math.max(0, (int) min.x); x < Math.min(size, (int) max.x + 1); x++) {
            for (int y = Math.max(0, (int) min.y); y < Math.min(size, (int) max.y + 1); y++) {
                for (int z = Math.max(0, (int) min.z); z < Math.min(size, (int) max.z + 1); z++) {
                    if (solid(x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
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

//    public int getLOD(int x, int y, int z, int lod) {
//        if (children == null || size <= lod) {
//            return value;
//        }
//        int child = 0;
//        if (x >= size / 2) {
//            child += 1;
//        }
//        if (y >= size / 2) {
//            child += 2;
//        }
//        if (z >= size / 2) {
//            child += 4;
//        }
//        return children[child].get(x % (size / 2), y % (size / 2), z % (size / 2));
//    }
}
