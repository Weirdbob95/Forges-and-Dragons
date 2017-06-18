package graphics;

import behaviors.Other.FPSBehavior;
import static behaviors.Other.onRender;
import static behaviors.Other.onUpdate;
import chunk.Chunk;
import static chunk.Chunk.ALL_DIRS;
import chunk.SimplexNoiseChunkSupplier;
import chunk.World;
import static engine.Activatable.using;
import engine.Core;
import engine.Input;
import static engine.Main.moveCamera;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import opengl.*;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import test.SimpleRect;
import util.Resources;

public class SurfaceGroup {

    public static void main(String[] args) {
        Core.init();

        shader = Resources.loadShaderProgram("new_chunk");

        Surface s1 = new Surface();
        s1.colorTexture = new float[6][4][3];
        s1.colorTexture[0][0][0] = .5f;
        s1.colorTexture[0][0][2] = 1;
        s1.colorTexture[1][0][1] = .5f;
        s1.colorTexture[0][1][0] = .5f;
        s1.shadeTexture = new float[7][5];
        s1.corners = new Vector3i[]{new Vector3i(0, 0, 0), new Vector3i(6, 0, 0), new Vector3i(0, 4, 0), new Vector3i(6, 4, 0)};

        Surface s2 = new Surface();
        s2.colorTexture = new float[1][1][3];
        s2.colorTexture[0][0][0] = 1;
        s2.shadeTexture = new float[2][2];
        s2.corners = new Vector3i[]{new Vector3i(0, 0, 1), new Vector3i(1, 0, 1), new Vector3i(0, 1, 1), new Vector3i(1, 1, 1)};

        SurfaceGroup sg = new SurfaceGroup();
        sg.normal = ALL_DIRS.get(5);
        sg.surfaces = Arrays.asList(s1, s2);
        sg.init();

        onUpdate(dt -> {
            if (Input.keyJustPressed(GLFW_KEY_ESCAPE)) {
                Core.stopGame();
            }

            moveCamera(dt);
        });

        onRender(() -> {
            glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (Chunk.shaderProgram != null) {
                Chunk.shaderProgram.setUniform("projectionMatrix", Camera.getProjectionMatrix());
            }

            shader.setUniform("projectionMatrix", Camera.getProjectionMatrix());

            sg.render();
        });

        new FPSBehavior().create();

        new World(new SimplexNoiseChunkSupplier(Math.random())).create();

        new SimpleRect(new Vector3d(0, 0, -1)).create();

        Core.run();
    }

    public static ShaderProgram shader;

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

    public Vector3i normal;
    public List<Surface> surfaces;

    private void createBufferTextures() {
        float[] positionsData = new float[4 * surfaces.size()];
        float[] sizesData = new float[2 * surfaces.size()];
        float[] colorIndicesData = new float[surfaces.size()];
        float[] shadeIndicesData = new float[surfaces.size()];

        for (int i = 0; i < surfaces.size(); i++) {
            Vector3i minPos = surfaces.get(i).minPosition();
            positionsData[4 * i] = minPos.x;
            positionsData[4 * i + 1] = minPos.y;
            positionsData[4 * i + 2] = minPos.z;
            positionsData[4 * i + 3] = 0;
            sizesData[2 * i] = surfaces.get(i).colorTexture.length;
            sizesData[2 * i + 1] = surfaces.get(i).colorTexture[0].length;
            if (i > 0) {
                colorIndicesData[i] = colorIndicesData[i - 1] + surfaces.get(i - 1).numColors();
                shadeIndicesData[i] = shadeIndicesData[i - 1] + surfaces.get(i - 1).numShades();
            }
        }

        float[] colorsData = new float[3 * (int) (colorIndicesData[surfaces.size() - 1] + surfaces.get(surfaces.size() - 1).numColors())];
        float[] shadesData = new float[(int) (shadeIndicesData[surfaces.size() - 1] + surfaces.get(surfaces.size() - 1).numShades())];

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

        positions = new BufferTexture(0, GL_RGB32F, positionsData);
        sizes = new BufferTexture(1, GL_RG32F, sizesData);
        colorIndices = new BufferTexture(2, GL_R32F, colorIndicesData);
        colors = new BufferTexture(3, GL_RGB32F, colorsData);
        shadeIndices = new BufferTexture(4, GL_R32F, shadeIndicesData);
        shades = new BufferTexture(5, GL_R32F, shadesData);
    }

    private BufferTexture positions, sizes, colorIndices, colors, shadeIndices, shades;

    public void init() {
        createBufferTextures();

        List<Vector3i> verts = surfaces.stream().flatMap(s -> Stream.of(s.corners)).collect(Collectors.toList());
        List<Vector3i> uniqueVerts = new ArrayList(new HashSet(verts));
        List<Integer> inds = new LinkedList();
        int[] surfaceIDs = new int[4 * surfaces.size()];
        for (int i = 0; i < verts.size() / 4; i++) {
            for (int j : new int[]{0, 1, 2, 1, 2, 3}) {
                inds.add(uniqueVerts.indexOf(verts.get(4 * i + j)));
            }
            for (int j = 0; j < 4; j++) {
                surfaceIDs[uniqueVerts.indexOf(verts.get(4 * i + j))] = i;
            }
        }

        int[] vertices = uniqueVerts.stream().flatMapToInt(v -> IntStream.of(v.x, v.y, v.z)).toArray();
        int[] indices = inds.stream().mapToInt(i -> i).toArray();

        System.out.println(Arrays.toString(surfaceIDs));

        vao = VertexArrayObject.createVAO(() -> {
            new BufferObject(GL_ELEMENT_ARRAY_BUFFER, indices);

            new BufferObject(GL_ARRAY_BUFFER, vertices);
            glVertexAttribPointer(0, 3, GL_INT, false, 0, 0);
            glEnableVertexAttribArray(0);

            new BufferObject(GL_ARRAY_BUFFER, surfaceIDs);
            glVertexAttribPointer(1, 1, GL_INT, false, 0, 0);
            glEnableVertexAttribArray(1);
        });
    }

    private VertexArrayObject vao;

    public void render() {
        shader.setUniform("worldMatrix", Camera.camera.getWorldMatrix(new Vector3d(0)));

        shader.setUniform("normal", normal);
        positions.sendToUniform(shader, "positions");
        sizes.sendToUniform(shader, "sizes");
        colorIndices.sendToUniform(shader, "colorIndices");
        colors.sendToUniform(shader, "colors");
        shadeIndices.sendToUniform(shader, "shadeIndices");
        shades.sendToUniform(shader, "shades");

        using(Arrays.asList(shader, vao), () -> {
            glDrawElements(GL_TRIANGLES, 6 * surfaces.size(), GL_UNSIGNED_INT, 0);
        });
    }
}
