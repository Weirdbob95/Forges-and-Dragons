package game.attacks;

import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import game.AttackBehavior;
import game.attacks.Arrow.ArrowTwinkle;
import graphics.Sprite;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static util.MathUtils.direction;

public class Firebolt extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);
    public final AttackBehavior attack = require(AttackBehavior.class);

    @Override
    public void createInner() {
        collider.hitboxSize = new Vector2d(4, 4);
        sprite.sprite = Sprite.load("firebolt.png");
        sprite.rotation = direction(velocity.velocity);
        sprite.scale = 2;
        lifetime.lifetime = .75;
    }

    @Override
    public void destroyInner() {
        ArrowTwinkle at = new ArrowTwinkle();
        at.position.position = new Vector2d(position.position);
        at.animation.sprite.color = new Vector4d(1, .2, 0, 1);
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
}
