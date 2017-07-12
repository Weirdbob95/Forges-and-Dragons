package chunk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class BlockColumns {

    public static int AIR_COLOR = 0x1000000;
    public static int ANY_SOLID = 0x1000001;

    public static int arrayToColor(float[] array) {
        int r = (int) (255 * array[0]);
        int g = (int) (255 * array[1]);
        int b = (int) (255 * array[2]);
        return 0x10000 * r + 0x100 * g + b;
    }

    public static float[] colorToArray(int color) {
        float[] r = new float[3];
        for (int j = 2; j >= 0; j--) {
            r[j] = (color % 256) / 255.0f;
            color /= 256;
        }
        return r;
    }

    private final int size;
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

    public BlockColumns downsample2() {
        int newSize = size / 2 + 1;
        BlockColumns bc = new BlockColumns(newSize);
        for (int x = 0; x < newSize; x++) {
            for (int y = 0; y < newSize; y++) {
                List<TreeMap<Integer, Integer>> columns = new LinkedList();
                for (int c = 0; c < 4; c++) {
                    int x2 = 2 * (x - 1) + c % 2;
                    int y2 = 2 * (y - 1) + c / 2;
                    if (x2 >= -1 && x2 < size - 1 && y2 >= -1 && y2 < size - 1) {
                        columns.add(columnAt(x2, y2));
                    }
                }

                int minZ = columns.stream().mapToInt(c -> c.firstKey()).min().getAsInt();
                int maxZ = columns.stream().mapToInt(c -> c.lastKey()).max().getAsInt();
//                Set<Integer> zVals = new TreeSet();
//                for (TreeMap<Integer, Integer> c : columns) {
//                    zVals.addAll(c.keySet().stream().map(z -> z / 2).collect(Collectors.toList()));
//                }

                TreeMap<Integer, Integer> newColumn = new TreeMap();
                for (int z = minZ / 2 * 2 - 2; z <= maxZ + 3; z += 2) {
                    int z2 = z;
                    List<Integer> vals = columns.stream().flatMap(c -> Stream.of(columnValueAt(c, z2), columnValueAt(c, z2 + 1))).collect(Collectors.toList());
//                    vals.removeIf(i -> i == AIR_COLOR);
//                    if (vals.isEmpty()) {
                    if (vals.contains(AIR_COLOR)) {
                        newColumn.put(z / 2, AIR_COLOR);
                    } else if (vals.size() < 8) {
                        newColumn.put(z / 2, ANY_SOLID);
                    } else {

                        float r = (float) vals.stream().mapToDouble(i -> colorToArray(i)[0]).average().getAsDouble();
                        float g = (float) vals.stream().mapToDouble(i -> colorToArray(i)[1]).average().getAsDouble();
                        float b = (float) vals.stream().mapToDouble(i -> colorToArray(i)[2]).average().getAsDouble();
                        newColumn.put(z / 2, arrayToColor(new float[]{r, g, b}));
                    }
                }
                simplifyColumn(newColumn);
                bc.blockColumns[x][y] = newColumn;
//                for (int z = 0; z < newSize; z++) {
//                    int[] blocks = new int[8];
//                    for (int c = 0; c < 8; c++) {
//                        blocks[c] = getBlock(2 * (x - 1) + c % 2, 2 * (y - 1) + c / 2 % 2, 2 * (z - 1) + c / 4);
//                    }
//                    if (x == 0 || y == 0 || z == 0 || x == newSize - 1 || y == newSize - 1 || z == newSize - 1) {
//                        if (IntStream.of(blocks).allMatch(i -> i != 0)) {
//                            r.set(x - 1, y - 1, z - 1, IntStream.of(blocks).filter(i -> i != 0).findFirst().getAsInt());
//                        }
//                    } else {
//                        if (IntStream.of(blocks).anyMatch(i -> i != 0)) {
//                            r.set(x - 1, y - 1, z - 1, IntStream.of(blocks).filter(i -> i != 0).findFirst().getAsInt());
//                        }
//                    }
//                }
            }
        }
        return bc;
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
