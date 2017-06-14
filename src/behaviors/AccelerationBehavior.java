package behaviors;

import engine.Behavior;
import org.joml.Vector3d;

public class AccelerationBehavior extends Behavior {

    public final VelocityBehavior velocity = require(VelocityBehavior.class);

    public Vector3d acceleration = new Vector3d();

    @Override
    public void update(double dt) {
        velocity.velocity.add(acceleration.mul(dt, new Vector3d()));
    }
}
