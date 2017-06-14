package engine;

import java.io.File;
import opengl.Window;

public abstract class Core {

    private static long prevTime;
    private static boolean shouldClose;

    public static void init() {
        System.setProperty("org.lwjgl.librarypath", new File("native").getAbsolutePath());
        Window.initGLFW();
        Input.init();
    }

    public static void run() {
        while (!shouldClose && !Window.window.shouldClose()) {
            Input.nextFrame();
            Window.window.nextFrame();

            long time = System.nanoTime();
            double dt = (time - prevTime) / 1e9;
            prevTime = time;

            Behavior.getAll().forEach(b -> b.update(dt));
            Behavior.getAll().forEach(b -> b.render());
        }
        Behavior.getAll().stream().filter(b -> b.isRoot()).forEach(b -> b.destroy());
        Window.cleanupGLFW();
        System.exit(0);
    }

    public static void stopGame() {
        shouldClose = true;
    }
}
