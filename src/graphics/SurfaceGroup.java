package graphics;

import static engine.Activatable.using;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

public class SurfaceGroup {

    public static ShaderProgram shader;

    public Vector3i normal;
    public List<Surface> surfaces = new LinkedList();
    private int numSurfaces;

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
