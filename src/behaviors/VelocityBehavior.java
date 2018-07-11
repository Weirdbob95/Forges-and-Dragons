package behaviors;

import engine.Behavior;
import org.joml.Vector2d;

public class VelocityBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public Vector2d velocity = new Vector2d();

    @Override
    public void update(double dt) {
        position.position.add(velocity.mul(dt, new Vector2d()));
    }
}
