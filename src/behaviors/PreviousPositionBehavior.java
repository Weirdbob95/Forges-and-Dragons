package behaviors;

import engine.Behavior;
import org.joml.Vector3d;

public class PreviousPositionBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    private Vector3d v = new Vector3d();
    public Vector3d prevPos = new Vector3d();

    @Override
    public void update(double dt) {
        prevPos = new Vector3d(position.position);
    }

    @Override
    public double updateLayer() {
        return 10;
    }
}
