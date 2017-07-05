package engine;

import behaviors.Other.FPSBehavior;
import static behaviors.Other.onRender;
import static behaviors.Other.onUpdate;
import chunk.Chunk;
import chunk.ChunkSupplier;
import chunk.World;
import graphics.Camera;
import graphics.SurfaceGroup;
import org.joml.Vector3d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;
import player.Player;
import util.Resources;

public abstract class Main {

    public static World world;

    public static void main(String[] args) {
        Core.init();

        onUpdate(0, dt -> {
            if (Input.keyJustPressed(GLFW_KEY_ESCAPE)) {
                Core.stopGame();
            }

            //moveCamera(dt);
        });

        onRender(-10, () -> {
            glClearColor(0.6f, 0.8f, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (SurfaceGroup.shader == null) {
                SurfaceGroup.shader = Resources.loadShaderProgram("new_chunk");
            }
            SurfaceGroup.shader.setUniform("projectionMatrix", Camera.getProjectionMatrix());
        });

        new FPSBehavior().create();

        world = new World(new ChunkSupplier());
        world.create();

        Player p = new Player();
        p.position.position = new Vector3d(Chunk.SIDE_LENGTH / 2, Chunk.SIDE_LENGTH / 2, ChunkSupplier.MAX_Z * Chunk.SIDE_LENGTH);
        p.create();

        Core.run();
    }
}
