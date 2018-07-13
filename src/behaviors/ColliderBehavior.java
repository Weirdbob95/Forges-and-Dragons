package behaviors;

import engine.Behavior;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector2d;

public class ColliderBehavior extends Behavior {

    private static final Collection<ColliderBehavior> ALL_COLLIDERS = track(ColliderBehavior.class);

    public final PositionBehavior position = require(PositionBehavior.class);

    public Vector2d hitboxSize = new Vector2d();
    public boolean isSolid = false;

    public List<ColliderBehavior> allTouchingAt(Vector2d pos) {
        Vector2d oldPos = position.position;
        position.position = pos;
        List<ColliderBehavior> r = new LinkedList();
        for (ColliderBehavior cb : ALL_COLLIDERS) {
            if (cb != this && intersects(cb)) {
                r.add(cb);
            }
        }
        position.position = oldPos;
        return r;
    }

    public <T extends Behavior> T findTouching(Class<T> c) {
        return allTouchingAt(position.position).stream()
                .map(b -> b.getOrNull(c))
                .filter(b -> b != null)
                .findAny().orElse(null);
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
        return solidCollisionAt(position.position);
    }

    public boolean solidCollisionAt(Vector2d pos) {
        return allTouchingAt(pos).stream().anyMatch(cb -> cb.isSolid);
    }
}
