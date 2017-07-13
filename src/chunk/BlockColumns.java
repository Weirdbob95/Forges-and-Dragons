package chunk;

import java.util.Iterator;
import java.util.TreeMap;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.MathUtils.AIR_COLOR;

public class BlockColumns {

    public final int size;
    private final TreeMap<Integer, Integer>[][] blockColumns;

    private boolean recomputeMinMax = true;
    private int minZ, maxZ;

    public BlockColumns(int size) {
        this.size = size;
        blockColumns = new TreeMap[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                blockColumns[x][y] = new TreeMap();
            }
        }
    }

    public boolean blockRangeEquals(int x, int y, int zMin, int zMax, int color) {
        return columnAt(x, y).subMap(zMin, zMax).isEmpty() && getBlock(x, y, zMax) == color;
    }

    private TreeMap<Integer, Integer> columnAt(int x, int y) {
        return blockColumns[x + 1][y + 1];
    }

    private static int columnValueAt(TreeMap<Integer, Integer> c, int z) {
        if (c.isEmpty() || c.lastKey() < z) {
            return AIR_COLOR;
        } else {
            return c.ceilingEntry(z).getValue();
        }
    }

    public int getBlock(int x, int y, int z) {
        return columnValueAt(columnAt(x, y), z);
    }

    public int getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public int maxZ() {
        recomputeMinMax();
        return maxZ;
    }

    public int minZ() {
        recomputeMinMax();
        return minZ;
    }

    private void recomputeMinMax() {
        if (recomputeMinMax) {
            minZ = Integer.MAX_VALUE;
            maxZ = Integer.MIN_VALUE;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (!blockColumns[x][y].isEmpty()) {
                        minZ = Math.min(minZ, blockColumns[x][y].firstKey());
                        maxZ = Math.max(maxZ, blockColumns[x][y].lastKey());
                    }
                }
            }
            recomputeMinMax = false;
        }
    }

    public void setBlock(int x, int y, int z, int color) {
        if (getBlock(x, y, z) != color) {
            int prevLowerColor = getBlock(x, y, z - 1);
            columnAt(x, y).put(z, color);
            if (prevLowerColor != color) {
                columnAt(x, y).put(z - 1, prevLowerColor);
            }
            recomputeMinMax = true;
        }
    }

    public void setBlockRange(int x, int y, int zMin, int zMax, int color) {
        int prevLowerColor = getBlock(x, y, zMin - 1);
        columnAt(x, y).subMap(zMin, true, zMax, true).clear();
        if (getBlock(x, y, zMax) != color) {
            columnAt(x, y).put(zMax, color);
        }
        if (prevLowerColor != color) {
            columnAt(x, y).put(zMin - 1, prevLowerColor);
        }
        recomputeMinMax = true;
    }

    public void setBlockRangeInfinite(int x, int y, int zMax, int color) {
        columnAt(x, y).headMap(zMax, true).clear();
        if (getBlock(x, y, zMax) != color) {
            columnAt(x, y).put(zMax, color);
        }
        recomputeMinMax = true;
    }

    private static void simplifyColumn(TreeMap<Integer, Integer> c) {
        if (c.size() < 2) {
            return;
        }
        Iterator<Integer> i = c.descendingKeySet().iterator();
        int prevZ = i.next();
        while (i.hasNext()) {
            int z = i.next();
            if (c.get(z).equals(c.get(prevZ))) {
                i.remove();
            } else {
                prevZ = z;
            }
        }
    }

    public boolean solid(Vector3i pos) {
        return getBlock(pos) != AIR_COLOR;
    }

    public boolean solid(Vector3d min, Vector3d max) {
        for (int x = Math.max(0, (int) Math.floor(min.x)); x <= Math.min(size - 3, (int) Math.ceil(max.x)); x++) {
            for (int y = Math.max(0, (int) Math.floor(min.y)); y <= Math.min(size - 3, (int) Math.ceil(max.y)); y++) {
                if (!blockRangeEquals(x, y, (int) Math.floor(min.z), (int) Math.ceil(max.z), AIR_COLOR)) {
                    return true;
                }
            }
        }
        return false;
    }
}
