package behaviors;

import engine.Behavior;
import graphics.RepeatedSprite;
import org.joml.Vector4d;

public class RepeatedSpriteBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public RepeatedSprite repeatedSprite = null;
    public double rotation = 0;
    public double scale = 1;
    public Vector4d color = new Vector4d(1, 1, 1, 1);

    @Override
    public void render() {
        if (repeatedSprite != null) {
            repeatedSprite.draw(position.position, rotation, scale, color);
        }
    }
}
