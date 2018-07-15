package behaviors;

import engine.Behavior;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector2d;
import static util.MathUtils.divide;

public class ColliderBehavior extends Behavior {

    private static final Collection<ColliderBehavior> ALL_COLLIDERS = track(ColliderBehavior.class);
    private static final Collection<ColliderBehavior> SOLID_COLLIDERS = new HashSet();

    public final PositionBehavior position = require(PositionBehavior.class);

    public CollisionShape collisionShape = null;
    private boolean isSolid = false;

    @Override
    public void destroyInner() {
        if (isSolid) {
            SOLID_COLLIDERS.remove(this);
        }
    }

    public void setSolid(boolean isSolid) {
        if (isSolid && !this.isSolid) {
            SOLID_COLLIDERS.add(this);
        } else if (!isSolid && this.isSolid) {
            SOLID_COLLIDERS.remove(this);
        }
        this.isSolid = isSolid;
    }

    public static abstract class CollisionShape {

        public PositionBehavior position;

        public CollisionShape(PositionBehavior position) {
            this.position = position;
        }

        public List<ColliderBehavior> allTouchingAt(Vector2d pos) {
            Vector2d oldPos = position.position;
            position.position = pos;
            List<ColliderBehavior> r = new LinkedList();
            for (ColliderBehavior cb : ALL_COLLIDERS) {
                if (cb.collisionShape != this) {
                    if (this instanceof Rectangle ? cb.collisionShape.intersects((Rectangle) this) : intersects((Rectangle) cb.collisionShape)) {
                        r.add(cb);
                    }
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

        public abstract boolean intersects(Rectangle other);

        public boolean solidCollision() {
            return solidCollisionAt(position.position);
        }

        public boolean solidCollisionAt(Vector2d pos) {
            Vector2d oldPos = position.position;
            position.position = pos;
            for (ColliderBehavior cb : SOLID_COLLIDERS) {
                if (cb.collisionShape != this) {
                    if (this instanceof Rectangle ? cb.collisionShape.intersects((Rectangle) this) : intersects((Rectangle) cb.collisionShape)) {
                        position.position = oldPos;
                        return true;
                    }
                }
            }
            position.position = oldPos;
            return false;
        }
    }

    public static class Rectangle extends CollisionShape {

        public Vector2d hitboxSize;

        public Rectangle(PositionBehavior position, Vector2d hitboxSize) {
            super(position);
            this.hitboxSize = hitboxSize;
        }

        public Vector2d getLowerLeft() {
            return position.position.sub(hitboxSize, new Vector2d());
        }

        public Vector2d getUpperRight() {
            return position.position.add(hitboxSize, new Vector2d());
        }

        @Override
        public boolean intersects(Rectangle other) {
            return !(getLowerLeft().x > other.getUpperRight().x
                    || getLowerLeft().y > other.getUpperRight().y
                    || other.getLowerLeft().x > getUpperRight().x
                    || other.getLowerLeft().y > getUpperRight().y);
        }
    }

    public static class RectangleGrid extends CollisionShape {

        public boolean[][] wallArray;
        public Vector2d squareSize;

        public RectangleGrid(PositionBehavior position, boolean[][] wallArray, Vector2d squareSize) {
            super(position);
            this.wallArray = wallArray;
            this.squareSize = squareSize;
        }

        @Override
        public boolean intersects(Rectangle other) {
            Vector2d LL = divide(other.getLowerLeft().sub(position.position), squareSize);
            Vector2d UR = divide(other.getUpperRight().sub(position.position), squareSize);
            for (int i = Math.max((int) LL.x, 0); i < Math.min(UR.x, wallArray.length); i++) {
                for (int j = Math.max((int) LL.y, 0); j < Math.min(UR.y, wallArray[0].length); j++) {
                    if (wallArray[i][j]) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
