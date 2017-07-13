package graphics;

import chunk.Mesher;
import chunk.Mesher.Quad;
import static engine.Activatable.using;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import opengl.BufferObject;
import opengl.BufferTexture;
import opengl.ShaderProgram;
import opengl.VertexArrayObject;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static util.MathUtils.*;

public class SurfaceGroup {

    public static ShaderProgram shader;

    public Vector3i normal;
    public List<Surface> surfaces = new ArrayList();
    private int numSurfaces;
    private final int lod;

    public SurfaceGroup(int lod) {
        this.lod = lod;
    }

    public SurfaceGroup downsample2() {
        SurfaceGroup sg = new SurfaceGroup(lod * 2);
        sg.normal = normal;
        if (surfaces.isEmpty()) {
            return sg;
        }

        Map<Integer, List<Surface>> layers = new HashMap();
        for (Surface s : surfaces) {
            int layer = (int) Math.round((getComponent(s.corners[0], normal, 2) - dirPosNeg(normal) * .01) / sg.lod);
            layers.putIfAbsent(layer, new LinkedList());
            layers.get(layer).add(s);
        }

        layers.forEach((layer, surfaces) -> {
            // Find the bounds on the rectangle to compute
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (Surface s : surfaces) {
                minX = Math.min(minX, (int) Math.floor((double) getComponent(s.corners[0], normal, 0) / sg.lod));
                minY = Math.min(minY, (int) Math.floor((double) getComponent(s.corners[0], normal, 1) / sg.lod));
                maxX = Math.max(maxX, (int) Math.ceil((double) getComponent(s.corners[3], normal, 0) / sg.lod));
                maxY = Math.max(maxY, (int) Math.ceil((double) getComponent(s.corners[3], normal, 1) / sg.lod));
            }
            int minX2 = minX, minY2 = minY;
            int width = maxX - minX, height = maxY - minY;

            // Combine all of the surfaces into one layer
            int[][] numHits = new int[width][height];
            float[][][] colors = new float[width][height][3];
            int[][] numHits2 = new int[width + 1][height + 1];
            float[][] shade = new float[width + 1][height + 1];
            boolean[][] mesh = new boolean[width][height];
            for (Surface s : surfaces) {
                int sMinX = (int) Math.floor((double) getComponent(s.corners[0], normal, 0) / lod);
                int sMaxX = (int) Math.floor((double) getComponent(s.corners[3], normal, 0) / lod);
                int sMinY = (int) Math.floor((double) getComponent(s.corners[0], normal, 1) / lod);
                int sMaxY = (int) Math.floor((double) getComponent(s.corners[3], normal, 1) / lod);
                for (int x = sMinX; x < sMaxX; x++) {
                    for (int y = sMinY; y < sMaxY; y++) {
                        numHits[(int) Math.floor(x / 2.0) - minX][(int) Math.floor(y / 2.0) - minY]++;
                        for (int c = 0; c < 3; c++) {
                            colors[(int) Math.floor(x / 2.0) - minX][(int) Math.floor(y / 2.0) - minY][c] += s.colorTexture[x - sMinX][y - sMinY][c];
                        }
                        mesh[(int) Math.floor(x / 2.0) - minX][(int) Math.floor(y / 2.0) - minY] = true;
                    }
                }
                for (int x = sMinX; x <= sMaxX; x++) {
                    for (int y = sMinY; y <= sMaxY; y++) {
                        numHits2[(int) Math.floor(x / 2.0) - minX][(int) Math.floor(y / 2.0) - minY]++;
                        shade[(int) Math.floor(x / 2.0) - minX][(int) Math.floor(y / 2.0) - minY] += s.shadeTexture[x - sMinX][y - sMinY];
                    }
                }
            }

            // Turn the layer back into a surface list
            for (Quad q : Mesher.mesh(mesh)) {
                Surface s = q.createSurface(layer - dirPos(normal), normal);
                Stream.of(s.corners).forEach(c -> c.add(orderComponents(0, normal, minX2, minY2)).mul(sg.lod));
                sg.surfaces.add(s);
                for (int i = 0; i < q.w; i++) {
                    for (int j = 0; j < q.h; j++) {
                        for (int c = 0; c < 3; c++) {
                            s.colorTexture[i][j][c] = colors[q.x + i][q.y + j][c] / numHits[q.x + i][q.y + j];
                        }
                    }
                }
                for (int i = 0; i <= q.w; i++) {
                    for (int j = 0; j <= q.h; j++) {
                        if (shade[q.x + i][q.y + j] == 0 || numHits2[q.x + 1][q.y + j] == 0) {
                            s.shadeTexture[i][j] = 1;
                        } else {
                            s.shadeTexture[i][j] = shade[q.x + i][q.y + j] / numHits2[q.x + i][q.y + j];
                        }
                    }
                }
            }
        });

        return sg;
    }

    public void generateData() {
        numSurfaces = surfaces.size();

        if (numSurfaces == 0) {
            return;
        }

        positionsData = new float[3 * surfaces.size()];
        sizesData = new float[2 * surfaces.size()];
        colorIndicesData = new float[surfaces.size()];
        shadeIndicesData = new float[surfaces.size()];

        for (int i = 0; i < surfaces.size(); i++) {
            Vector3i minPos = surfaces.get(i).minPosition();
            positionsData[3 * i] = minPos.x;
            positionsData[3 * i + 1] = minPos.y;
            positionsData[3 * i + 2] = minPos.z;
            sizesData[2 * i] = surfaces.get(i).colorTexture.length;
            sizesData[2 * i + 1] = surfaces.get(i).colorTexture[0].length;
            if (i > 0) {
                colorIndicesData[i] = colorIndicesData[i - 1] + surfaces.get(i - 1).numColors();
                shadeIndicesData[i] = shadeIndicesData[i - 1] + surfaces.get(i - 1).numShades();
            }
        }

        colorsData = new float[3 * (int) (colorIndicesData[surfaces.size() - 1] + surfaces.get(surfaces.size() - 1).numColors())];
        shadesData = new float[(int) (shadeIndicesData[surfaces.size() - 1] + surfaces.get(surfaces.size() - 1).numShades())];

        for (int i = 0; i < surfaces.size(); i++) {
            for (int j = 0; j < surfaces.get(i).numColors(); j++) {
                for (int k = 0; k < 3; k++) {
                    colorsData[3 * ((int) colorIndicesData[i] + j) + k] = surfaces.get(i).getColorAt(j)[k];
                }
            }
            for (int j = 0; j < surfaces.get(i).numShades(); j++) {
                shadesData[(int) shadeIndicesData[i] + j] = surfaces.get(i).getShadeAt(j);
            }
        }
    }

    private float[] positionsData, sizesData, colorIndicesData, colorsData, shadeIndicesData, shadesData;

    public void init() {
        if (numSurfaces == 0 || vao != null) {
            return;
        }

        positions = new BufferTexture(0, GL_RGB32F, positionsData);
        sizes = new BufferTexture(1, GL_RG32F, sizesData);
        colorIndices = new BufferTexture(2, GL_R32F, colorIndicesData);
        colors = new BufferTexture(3, GL_RGB32F, colorsData);
        shadeIndices = new BufferTexture(4, GL_R32F, shadeIndicesData);
        shades = new BufferTexture(5, GL_R32F, shadesData);

        int[] vertices = surfaces.stream().flatMap(s -> Stream.of(s.corners[0], s.corners[1], s.corners[2], s.corners[1], s.corners[2], s.corners[3]))
                .flatMapToInt(v -> IntStream.of(v.x, v.y, v.z)).toArray();
        int[] surfaceIDs = IntStream.range(0, surfaces.size()).flatMap(x -> IntStream.of(x, x, x, x, x, x)).toArray();

        vao = VertexArrayObject.createVAO(() -> {
            new BufferObject(GL_ARRAY_BUFFER, vertices);
            glVertexAttribPointer(0, 3, GL_INT, false, 0, 0);
            glEnableVertexAttribArray(0);

            new BufferObject(GL_ARRAY_BUFFER, surfaceIDs);
            glVertexAttribPointer(1, 1, GL_INT, false, 0, 0);
            glEnableVertexAttribArray(1);
        });

        surfaces = null;
        positionsData = sizesData = colorIndicesData = colorsData = shadeIndicesData = shadesData = null;
    }

    private BufferTexture positions, sizes, colorIndices, colors, shadeIndices, shades;
    private VertexArrayObject vao;

    public void render() {
        if (numSurfaces == 0) {
            return;
        }

        init();

        shader.setUniform("normal", normal);
        positions.sendToUniform(shader, "positions");
        sizes.sendToUniform(shader, "sizes");
        colorIndices.sendToUniform(shader, "colorIndices");
        colors.sendToUniform(shader, "colors");
        shadeIndices.sendToUniform(shader, "shadeIndices");
        shades.sendToUniform(shader, "shades");

        using(Arrays.asList(shader, vao), () -> {
            glDrawArrays(GL_TRIANGLES, 0, 6 * numSurfaces);
        });
    }

    public static class Surface {

        public Vector3i[] corners;
        public float[][][] colorTexture;
        public float[][] shadeTexture;

        public float[] getColorAt(int j) {
            return colorTexture[j % colorTexture.length][j / colorTexture.length];
        }

        public float getShadeAt(int j) {
            return shadeTexture[j % shadeTexture.length][j / shadeTexture.length];
        }

        public Vector3i minPosition() {
            return new Vector3i(Stream.of(corners).mapToInt(v -> v.x).min().getAsInt(),
                    Stream.of(corners).mapToInt(v -> v.y).min().getAsInt(),
                    Stream.of(corners).mapToInt(v -> v.z).min().getAsInt());
        }

        public int numColors() {
            return colorTexture.length * colorTexture[0].length;
        }

        public int numShades() {
            return shadeTexture.length * shadeTexture[0].length;
        }
    }
}
