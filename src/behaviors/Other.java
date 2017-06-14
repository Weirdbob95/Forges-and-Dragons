package behaviors;

import engine.Behavior;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import opengl.Window;

public abstract class Other {

    public static Behavior onMainThread(Runnable toRun) {
        return new Behavior() {
            @Override
            public void update(double dt) {
                toRun.run();
                destroy();
            }
        }.create();
    }

    public static Behavior onRender(Runnable toRun) {
        return new Behavior() {
            @Override
            public void render() {
                toRun.run();
            }
        }.create();
    }

    public static Behavior onUpdate(Consumer<Double> toRun) {
        return new Behavior() {
            @Override
            public void update(double dt) {
                toRun.accept(dt);
            }
        }.create();
    }

    public static class FPSBehavior extends Behavior {

        private final Queue<Double> dtList = new LinkedList();
        public double fps;

        @Override
        public void update(double dt) {
            dtList.add(dt);
            if (dtList.size() > 10) {
                dtList.remove();
            }
            fps = 10 / dtList.stream().mapToDouble(x -> x).sum();
            Window.window.setTitle("FPS: " + Math.round(fps));
        }
    }
}
