package behaviors;

import engine.Behavior;
import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector2d;

public class ColliderBehavior extends Behavior {

    private static final Collection<ColliderBehavior> ALL_COLLIDERS = new LinkedList();

    public final PositionBehavior position = require(PositionBehavior.class);

    public Vector2d hitboxSize = new Vector2d();
    public boolean isSolid = false;

    @Override
    public void createInner() {
        ALL_COLLIDERS.add(this);
    }

    @Override
    public void destroyInner() {
        ALL_COLLIDERS.remove(this);
    }

    public Vector2d getLowerLeft() {
        return position.position.sub(hitboxSize, new Vector2d());
    }

    public Vector2d getUpperRight() {
        return position.position.add(hitboxSize, new Vector2d());
    }

    public boolean intersects(ColliderBehavior other) {
        return !(getLowerLeft().x > other.getUpperRight().x
                || getLowerLeft().y > other.getUpperRight().y
                || other.getLowerLeft().x > getUpperRight().x
                || other.getLowerLeft().y > getUpperRight().y);
    }

    public boolean solidCollision() {
        for (ColliderBehavior other : ALL_COLLIDERS) {
            if (other.isSolid) {
                if (other != this) {
                    if (intersects(other)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean solidCollisionAt(Vector2d pos) {
        Vector2d oldPos = position.position;
        position.position = pos;
        for (ColliderBehavior other : ALL_COLLIDERS) {
            if (other.isSolid) {
                if (other != this) {
                    if (intersects(other)) {
                        position.position = oldPos;
                        return true;
                    }
                }
            }
        }
        position.position = oldPos;
        return false;
    }
}
