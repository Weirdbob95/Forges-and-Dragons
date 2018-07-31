package game;

import behaviors.ColliderBehavior;
import behaviors.ColliderBehavior.Rectangle;
import behaviors.SpriteBehavior;
import engine.Behavior;
import graphics.Sprite;
import org.joml.Vector2d;

public class Coin extends Behavior {

    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);

    @Override
    public void createInner() {
        sprite.sprite = Sprite.load("coin.png");
        sprite.scale = .5;
        collider.collisionShape = new Rectangle(collider.position, new Vector2d(4, 4));
    }

    @Override
    public void update(double dt) {
//         if (collider.collisionShape.findTouching(Player.class) != null) {
//            destroy();
//        }
    }
}
