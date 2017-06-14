package behaviors;

import engine.Behavior;
import org.joml.Vector3d;

public class VelocityBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public Vector3d velocity = new Vector3d();

    @Override
    public void update(double dt) {
        position.position.add(velocity.mul(dt, new Vector3d()));
    }
}
