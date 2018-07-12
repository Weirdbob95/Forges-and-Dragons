package game;

import behaviors.ColliderBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import engine.Behavior;
import graphics.Sprite;
import org.joml.Vector2d;

public class Wall extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    @Override
    public void createInner() {
        collider.hitboxSize = new Vector2d(16, 16);
        collider.isSolid = true;
        sprite.sprite = Sprite.load("wall.png");
    }
}
