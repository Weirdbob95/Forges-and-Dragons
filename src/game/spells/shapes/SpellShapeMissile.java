package game.spells.shapes;

import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import static engine.Behavior.track;
import game.Creature;
import game.spells.SpellInfo;
import game.spells.TypeDefinitions.SpellShapeInitial;
import java.util.Collection;
import org.joml.Vector2d;
import static util.MathUtils.direction;
import static util.MathUtils.mod;
import static util.MathUtils.rotate;
import static util.MathUtils.unitVector;

public abstract class SpellShapeMissile extends SpellShapeInitial {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    public boolean isHoming;
    public boolean isMultishot;
    public boolean isPiercing;
    public boolean isBouncing;

    public void spawnMissiles(SpellInfo info, Vector2d goal, Class<? extends Behavior> c, double velocity) {
        try {
            for (int i = 0; i < (isMultishot ? 5 : 1); i++) {
                Behavior b = c.newInstance();
                MissileBehavior mb = b.get(MissileBehavior.class);
                mb.position.position = info.position();
                mb.velocity.velocity = unitVector(info.direction + (isMultishot ? Math.random() - .5 : 0)).mul(velocity);
                mb.spellShape = this;
                mb.info = info.multiplyPower(isMultishot ? .25 : 1).multiplyPower(isHoming ? .75 : 1);
                b.create();
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class MissileBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final VelocityBehavior velocity = require(VelocityBehavior.class);
        public final ColliderBehavior collider = require(ColliderBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        public SpellShapeMissile spellShape;
        public SpellInfo info;
        public double homingRate;

        @Override
        public void update(double dt) {
            if (collider.collisionShape.solidCollision()) {
                getRoot().destroy();
            }
            Creature c = collider.collisionShape.findTouching(Creature.class);
            if (c != null && c != info.target.creature) {
                spellShape.hit(info.setTarget(c));
                getRoot().destroy();
            }
            if (spellShape.isHoming) {
                Creature target = null;
                for (Creature c2 : ALL_CREATURES) {
                    if (c2 != info.target.creature) {
                        if (target == null || c2.position.position.distance(position.position) < target.position.position.distance(position.position)) {
                            target = c2;
                        }
                    }
                }
                if (target != null && target.position.position.distance(position.position) < 300) {
                    double angleDelta = mod(direction(target.position.position.sub(position.position, new Vector2d())) - direction(velocity.velocity), 2 * Math.PI);
                    if (angleDelta < Math.PI / 2) {
                        velocity.velocity = rotate(velocity.velocity, Math.min(angleDelta, homingRate * dt));
                    } else if (angleDelta > 3. / 2 * Math.PI) {
                        velocity.velocity = rotate(velocity.velocity, -Math.min(2 * Math.PI - angleDelta, homingRate * dt));
                    }
                }
            }
        }
    }
}
