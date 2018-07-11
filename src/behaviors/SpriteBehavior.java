package behaviors;

import engine.Behavior;
import graphics.Sprite;

public class SpriteBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public Sprite sprite = null;
    public double rotation = 0;
    public double scale = 1;

    @Override
    public void render() {
        if (sprite != null) {
            sprite.draw(position.position, rotation, scale);
        }
    }
}
