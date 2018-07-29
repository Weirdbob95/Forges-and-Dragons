package game;

import behaviors.LifetimeBehavior;
import engine.Behavior;

public class GraphicsEffect extends Behavior {

    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

    public Runnable onRender;

    public static void createGraphicsEffect(double lifetime, Runnable onRender) {
        GraphicsEffect ge = new GraphicsEffect();
        ge.lifetime.lifetime = lifetime;
        ge.onRender = onRender;
        ge.create();
    }

    @Override
    public void render() {
        onRender.run();
    }
}
