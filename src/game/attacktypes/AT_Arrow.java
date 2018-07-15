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
import static util.MathUtils.direction;

public class AT_Arrow extends AttackType {

    @Override
    public void attack() {
        ArrowBehavior a = new ArrowBehavior();
        a.position.position = new Vector2d(attacker.position.position);
        a.velocity.velocity = attacker.targetPos.sub(attacker.position.position).normalize().mul(1000);
        a.target = attacker.target;
        a.damage = (Math.random() + .5) * (5 + charge * 20) * .5;
        a.create();
    }

    @Override
    public double cooldown() {
        return .3;
    }

    @Override
    public boolean fireAtMaxCharge() {
        return false;
    }

    @Override
    public boolean payChargeCost(double dt) {
        return attacker.creature.stamina.pay(10 * dt);
    }

    @Override
    public boolean payInitialCost() {
        return attacker.creature.stamina.pay(10);
    }

    @Override
    public double maxCharge() {
        return 2;
    }

    @Override
    public double minCharge() {
        return .2;
    }

    @Override
    public double slowdown() {
        return .5;
    }

    public static class ArrowBehavior extends Behavior {

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
            sprite.sprite = Sprite.load("arrow.png");
            sprite.rotation = direction(velocity.velocity);
            lifetime.lifetime = .5;
        }

        @Override
        public void destroyInner() {
            Twinkle at = new Twinkle();
            at.position.position = new Vector2d(position.position);
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
