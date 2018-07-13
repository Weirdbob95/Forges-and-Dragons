package game.attacks;

import behaviors.AnimationBehavior;
import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import game.AttackBehavior;
import graphics.Animation;
import graphics.Sprite;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class Arrow extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);
    public final AttackBehavior attack = require(AttackBehavior.class);

    @Override
    public void createInner() {
        collider.hitboxSize = new Vector2d(4, 4);
        sprite.sprite = Sprite.load("arrow.png");
        sprite.rotation = direction(velocity.velocity);
        lifetime.lifetime = .5;
    }

    @Override
    public void destroyInner() {
        ArrowTwinkle at = new ArrowTwinkle();
        at.position.position = new Vector2d(position.position);
        at.create();
    }

    @Override
    public void update(double dt) {
        if (collider.solidCollision()) {
            destroy();
        }
        Behavior b = collider.findTouching(attack.target);
        if (b != null) {
            attack.hit(b);
            destroy();
        }
    }

    public static class ArrowTwinkle extends Behavior {

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
}
