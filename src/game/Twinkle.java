package game;

import behaviors.AnimationBehavior;
import behaviors.PositionBehavior;
import engine.Behavior;
import graphics.Animation;

public class Twinkle extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final AnimationBehavior animation = require(AnimationBehavior.class);

    @Override
    public void createInner() {
        animation.animation = new Animation("twinkle_anim");
        animation.loop = false;
        animation.sprite.scale = 2;
    }

    @Override
    public void update(double dt) {
        if (animation.animSpeed == 0) {
            destroy();
        }
    }
}
