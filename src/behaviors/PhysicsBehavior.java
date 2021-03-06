package behaviors;

import engine.Behavior;
import org.joml.Vector2d;

public class PhysicsBehavior extends Behavior {

    private static final int DETAIL = 5;

    public final PositionBehavior position = require(PositionBehavior.class);
    public final PreviousPositionBehavior prevPos = require(PreviousPositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final ColliderBehavior collider = require(ColliderBehavior.class);

    private boolean moveToWall(Vector2d del) {
        if (!collider.collisionShape.solidCollisionAt(position.position.add(del, new Vector2d()))) {
            position.position.add(del);
            return false;
        }
        double best = 0;
        double check = .5;
        double step = .25;
        for (int i = 0; i < DETAIL; i++) {
            if (collider.collisionShape.solidCollisionAt(del.mul(check, new Vector2d()).add(position.position))) {
                check -= step;
            } else {
                best = check;
                check += step;
            }
            step /= 2;
        }
        position.position.add(del.mul(best, new Vector2d()));
        return true;
    }

    @Override
    public void update(double dt) {
        Vector2d del = position.position.sub(prevPos.prevPos, new Vector2d());
        if (collider.collisionShape.solidCollisionAt(position.position)) {
            if (!collider.collisionShape.solidCollisionAt(prevPos.prevPos)) {
                position.position = new Vector2d(prevPos.prevPos);

                if (moveToWall(new Vector2d(del.x, 0))) {
                    velocity.velocity.x = 0;
                }
                if (moveToWall(new Vector2d(0, del.y))) {
                    velocity.velocity.y = 0;
                }
            } else {
                velocity.velocity.set(0);
            }
        }
    }

    @Override
    public double updateLayer() {
        return 5;
    }
}
