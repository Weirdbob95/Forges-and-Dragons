package behaviors;

import engine.Behavior;
import org.joml.Vector2d;

public class AccelerationBehavior extends Behavior {

    public final VelocityBehavior velocity = require(VelocityBehavior.class);

    public Vector2d acceleration = new Vector2d();

    @Override
    public void update(double dt) {
        velocity.velocity.add(acceleration.mul(dt, new Vector2d()));
    }
}
