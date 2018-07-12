package behaviors;

import engine.Behavior;
import org.joml.Vector2d;

public class SpaceOccupierBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);

    public double radius = 30;
    public double lightness = 1;

    @Override
    public void update(double dt) {
        for (SpaceOccupierBehavior other : getAllOfType(SpaceOccupierBehavior.class)) {
            if (other != this) {
                Vector2d delta = other.position.position.sub(position.position, new Vector2d());
                double distance = delta.length();
                if (distance < radius + other.radius) {
                    delta.normalize();
                    position.position.sub(delta.mul((radius + other.radius - distance) * lightness * dt, new Vector2d()));
                    other.position.position.add(delta.mul((radius + other.radius - distance) * other.lightness * dt, new Vector2d()));
                }
            }
        }
    }
}
