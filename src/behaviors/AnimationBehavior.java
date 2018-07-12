package behaviors;

import engine.Behavior;
import graphics.Animation;
import static util.MathUtils.mod;

public class AnimationBehavior extends Behavior {

    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    public Animation animation = null;
    public String animMode = "";
    public double animProgress = 0;
    public double animSpeed = 1;
    public boolean loop = true;

    @Override
    public void update(double dt) {
        animProgress += animation.speed * animSpeed * dt;
        if (animProgress < 0 || animProgress >= animation.length) {
            if (loop) {
                animProgress = mod(animProgress, animation.length);
            } else {
                animProgress -= animation.speed * animSpeed * dt;
                animSpeed = 0;
            }
        }
        if (animation == null) {
            sprite.sprite = null;
        } else {
            sprite.sprite = animation.getSpriteOrNull(animMode, (int) animProgress);
        }
    }
}
