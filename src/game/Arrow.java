package game;

import behaviors.ColliderBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import graphics.Sprite;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class Arrow extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    public double lifetime = .5;

    @Override
    public void createInner() {
        collider.hitboxSize = new Vector2d(4, 4);
        sprite.sprite = new Sprite("arrow.png");
        sprite.rotation = direction(velocity.velocity);
    }

    @Override
    public void update(double dt) {
        lifetime -= dt;
        if (lifetime < 0) {
            destroy();
        }
        if (collider.solidCollision()) {
            destroy();
        }
    }
}
