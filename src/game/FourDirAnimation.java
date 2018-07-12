package game;

import behaviors.AnimationBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import java.util.function.Supplier;
import org.joml.Vector2d;

public class FourDirAnimation extends Behavior {

    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final AnimationBehavior animation = require(AnimationBehavior.class);

    public Supplier<Vector2d> directionSupplier = () -> velocity.velocity;
    public Supplier<Boolean> playAnimSupplier = () -> directionSupplier.get().length() > 10;

    @Override
    public void createInner() {
        animation.animMode = "down";
    }

    @Override
    public void update(double dt) {
        Vector2d direction = directionSupplier.get();
        if (direction.length() > 10) {
            if (direction.x > Math.abs(direction.y)) {
                animation.animMode = "right";
            } else if (direction.x < -Math.abs(direction.y)) {
                animation.animMode = "left";
            } else if (direction.y > Math.abs(direction.x)) {
                animation.animMode = "up";
            } else if (direction.y < -Math.abs(direction.x)) {
                animation.animMode = "down";
            }
        }
        if (playAnimSupplier.get()) {
            animation.animSpeed = 1;
        } else {
            animation.animSpeed = 0;
            animation.animProgress = 0;
        }
    }
}
