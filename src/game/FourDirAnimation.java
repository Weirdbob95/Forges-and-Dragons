package game;

import behaviors.AnimationBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import graphics.Animation;

public class FourDirAnimation extends Behavior {

    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final AnimationBehavior animation = require(AnimationBehavior.class);

    public String direction = "down";
    public String fileName = null;

    @Override
    public void createInner() {

    }

    @Override
    public void update(double dt) {
        if (velocity.velocity.length() > 1e-3) {
            if (velocity.velocity.x > Math.abs(velocity.velocity.y)) {
                direction = "right";
            }
        }
        if (fileName != null) {
            animation.animation = new Animation(fileName + "/" + direction + "_anim");
        }
    }
}
