package game.attacks;

import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import engine.Behavior;
import game.AttackBehavior;
import graphics.Sprite;
import org.joml.Vector2d;
import static util.MathUtils.unitVector;

public class SwordSwing extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);
    public final AttackBehavior attack = require(AttackBehavior.class);

    @Override
    public void createInner() {
        sprite.sprite = Sprite.load("slash.png");
        sprite.scale = 1;
        collider.hitboxSize = new Vector2d(24, 24);

        Vector2d swingPos = unitVector(sprite.rotation).mul(30).add(position.position);
        for (Behavior b : collider.allTouchingAt(swingPos)) {
            if (b.getOrNull(attack.target) != null) {
                attack.hit(b);
            }
        }
    }
}
