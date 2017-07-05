package behaviors;

import engine.Behavior;
import engine.Main;
import org.joml.Vector3d;

public class PhysicsBehavior extends Behavior {

    private static final int DETAIL = 5;

    public final PositionBehavior position = require(PositionBehavior.class);
    public final PreviousPositionBehavior prevPos = require(PreviousPositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);

    public Vector3d hitboxSize = new Vector3d();
    public boolean onGround;

    private boolean moveToWall(Vector3d del) {
        if (!wouldCollideAt(position.position.add(del, new Vector3d()))) {
            position.position.add(del);
            return false;
        }
        double best = 0;
        double check = .5;
        double step = .25;
        for (int i = 0; i < DETAIL; i++) {
            if (wouldCollideAt(del.mul(check, new Vector3d()).add(position.position))) {
                check -= step;
            } else {
                best = check;
                check += step;
            }
            step /= 2;
        }
        position.position.add(del.mul(best, new Vector3d()));
        return true;
    }

    @Override
    public void update(double dt) {
        onGround = false;
        Vector3d del = position.position.sub(prevPos.prevPos, new Vector3d());
        if (wouldCollideAt(position.position)) {
            if (!wouldCollideAt(prevPos.prevPos)) {
                position.position = new Vector3d(prevPos.prevPos);
                if (moveToWall(new Vector3d(0, 0, del.z))) {
                    velocity.velocity.z = 0;
                    onGround = true;
                }

                // Step up walls
                double z = position.position.z;
                if (onGround) {
                    moveToWall(new Vector3d(0, 0, 1));
                }

                if (moveToWall(new Vector3d(del.x, 0, 0))) {
                    velocity.velocity.x = 0;
                }
                if (moveToWall(new Vector3d(0, del.y, 0))) {
                    velocity.velocity.y = 0;
                }

                if (position.position.z != z) {
                    moveToWall(new Vector3d(0, 0, z - position.position.z));
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

    public boolean wouldCollideAt(Vector3d position) {
        return Main.world.collides(position.sub(hitboxSize, new Vector3d()), position.add(hitboxSize, new Vector3d()));
    }
}
