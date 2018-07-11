package behaviors;

import engine.Behavior;
import org.joml.Vector2d;

public class PreviousPositionBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    private Vector2d v = new Vector2d();
    public Vector2d prevPos = new Vector2d();

    @Override
    public void update(double dt) {
        prevPos = new Vector2d(position.position);
    }

    @Override
    public double updateLayer() {
        return 10;
    }
}
