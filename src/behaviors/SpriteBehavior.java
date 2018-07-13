package behaviors;

import engine.Behavior;
import graphics.Sprite;
import org.joml.Vector4d;

public class SpriteBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public Sprite sprite = null;
    public double rotation = 0;
    public double scale = 1;
    public Vector4d color = new Vector4d(1, 1, 1, 1);

    @Override
    public void render() {
        if (sprite != null) {
            sprite.draw(position.position, rotation, scale, color);
        }
    }
}
