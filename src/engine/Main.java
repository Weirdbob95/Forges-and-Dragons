package engine;

import behaviors.Other.FPSBehavior;
import static behaviors.Other.onRender;
import static behaviors.Other.onUpdate;
import game.DungeonGenerator;
import game.Monster;
import game.Player;
import graphics.Camera;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.opengl.GL11.*;

public abstract class Main {

    public static void main(String[] args) {
        Core.init();

        onUpdate(0, dt -> {
            if (Input.keyJustPressed(GLFW_KEY_ESCAPE)) {
                Core.stopGame();
            }
        });

        onRender(-10, () -> {
            glClearColor(0.8f, 0.8f, 0.8f, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        });

        Player p = new Player();
        p.position.position = new Vector2d(-50, -50);
        p.create();

        new DungeonGenerator().createLevel(20, 20);

        onUpdate(0, dt -> {
            Camera.camera.zoom *= Math.pow(1.1, Input.mouseWheel());
        });

        onUpdate(0, dt -> {
            if (Input.keyJustPressed(GLFW_KEY_M)) {
                Monster m = new Monster();
                m.position.position = Input.mouseWorld();
                m.create();
            }
        });

        new FPSBehavior().create();

        Core.run();
    }
}
