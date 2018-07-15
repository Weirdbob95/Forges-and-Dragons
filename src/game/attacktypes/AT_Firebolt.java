package game.attacktypes;

import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import game.AttackType;
import game.Creature;
import game.Twinkle;
import graphics.Sprite;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static util.MathUtils.direction;

public class AT_Firebolt extends AttackType {

    @Override
    public void attack() {
        FireboltBehavior a = new FireboltBehavior();
        a.position.position = new Vector2d(attacker.position.position);
        a.velocity.velocity = attacker.targetPos.sub(attacker.position.position).normalize().mul(600);
        a.target = attacker.target;
        a.damage = (Math.random() + .5) * 60;
        a.create();
    }

    @Override
    public double cooldown() {
        return .3;
    }

    @Override
    public boolean fireAtMaxCharge() {
        return true;
    }

    @Override
    public boolean payChargeCost(double dt) {
        return true;
    }

    @Override
    public boolean payInitialCost() {
        return attacker.creature.mana.pay(20);
    }

    @Override
    public double maxCharge() {
        return .5;
    }

    @Override
    public double minCharge() {
        return .5;
    }

    @Override
    public double slowdown() {
        return .25;
    }

    public static class FireboltBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final VelocityBehavior velocity = require(VelocityBehavior.class);
        public final ColliderBehavior collider = require(ColliderBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        public Class<? extends Behavior> target = null;
        public double damage = 0;

        @Override
        public void createInner() {
            collider.collisionShape = new ColliderBehavior.Rectangle(position, new Vector2d(4, 4));
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
            Behavior b = collider.collisionShape.findTouching(target);
            if (b != null) {
                b.get(Creature.class).health.modify(-damage);
                destroy();
            }
        }
    }
}
