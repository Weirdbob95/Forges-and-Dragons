package game.spells.targets;

import behaviors.*;
import behaviors.ColliderBehavior.Rectangle;
import engine.Behavior;
import game.Creature;
import game.Twinkle;
import game.spells.SpellInstance;
import game.spells.SpellNode.SpellTarget;
import game.spells.SpellPosition.CreatureSpellPosition;
import graphics.Sprite;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class ST_Projectile extends SpellTarget {

    @Override
    public void cast(SpellInstance si) {
        ST_ProjectileBehavior spb = new ST_ProjectileBehavior();
        spb.position.position = si.position();
        spb.velocity.velocity = si.goal().sub(si.position(), new Vector2d()).normalize().mul(600);
        spb.projectile = this;
        spb.si = si;
        spb.create();
    }

    @Override
    public double personalCost() {
        return 0;
    }

    public static class ST_ProjectileBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final VelocityBehavior velocity = require(VelocityBehavior.class);
        public final ColliderBehavior collider = require(ColliderBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        public ST_Projectile projectile;
        public SpellInstance si;

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
            at.animation.sprite.color = si.color();
            at.create();
        }

        @Override
        public void update(double dt) {
            if (collider.collisionShape.solidCollision()) {
                projectile.not(si);
                destroy();
            }
            Creature c = collider.collisionShape.findTouching(Creature.class);
            if (c != null) {
                if (si.creature() != null && si.creature() != c) {
                    projectile.hit(si.setPosition(new CreatureSpellPosition(c)));
                    destroy();
                }
            }
        }
    }
}
