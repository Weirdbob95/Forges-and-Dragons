package game.spells;

import behaviors.ColliderBehavior;
import behaviors.ColliderBehavior.Rectangle;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import game.Creature;
import game.Twinkle;
import game.spells.TypeDefinitions.SpellShapeInitial;
import graphics.Sprite;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static util.MathUtils.direction;
import static util.MathUtils.unitVector;

public class ProjectileShape extends SpellShapeInitial {

    @Override
    public void cast(SpellInfo info, Vector2d goal) {
        ProjectileShapeBehavior psb = new ProjectileShapeBehavior();
        psb.position.position = info.position();
        psb.velocity.velocity = unitVector(info.direction).mul(600);
        psb.projectileShape = this;
        psb.info = info;
        psb.create();
    }

    public static class ProjectileShapeBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final VelocityBehavior velocity = require(VelocityBehavior.class);
        public final ColliderBehavior collider = require(ColliderBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        public ProjectileShape projectileShape;
        public SpellInfo info;

        @Override
        public void createInner() {
            collider.collisionShape = new Rectangle(position, new Vector2d(4, 4));
            sprite.sprite = Sprite.load("firebolt.png");
            sprite.rotation = direction(velocity.velocity);
            sprite.scale = 2;
            lifetime.lifetime = .75;
        }

        @Override
        public void destroyInner() {
            Twinkle at = new Twinkle();
            at.position.position = new Vector2d(position.position);
            at.animation.sprite.color = new Vector4d(1, .2, 0, 1);
            at.create();
        }

        @Override
        public void update(double dt) {
            if (collider.collisionShape.solidCollision()) {
                destroy();
            }
            Creature c = collider.collisionShape.findTouching(Creature.class);
            if (c != null && c != info.target.creature) {
                projectileShape.hit(info);
                destroy();
            }
        }
    }
}
