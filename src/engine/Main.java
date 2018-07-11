package engine;

import behaviors.Other.FPSBehavior;
import static behaviors.Other.onRender;
import static behaviors.Other.onUpdate;
import game.Monster;
import game.Player;
import game.Wall;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
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

        new Player().create();

        for (int i = 0; i < 10; i++) {
            Wall w = new Wall();
            w.position.position = new Vector2d(80, 32 * i);
            w.create();
        }

        Monster m = new Monster();
        m.position.position = new Vector2d(200, 200);
        m.create();

        new FPSBehavior().create();

        Core.run();
    }
}
