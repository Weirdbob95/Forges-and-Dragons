package game.attacktypes;

import behaviors.ColliderBehavior.CollisionShape;
import behaviors.ColliderBehavior.Rectangle;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import engine.Behavior;
import game.AttackType;
import game.Creature;
import graphics.Sprite;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class AT_SwordSwing extends AttackType {

    @Override
    public void attack() {
        SwordSwingBehavior ss = new SwordSwingBehavior();
        ss.position.position = attacker.position.position;
        ss.sprite.rotation = direction(attacker.targetPos.sub(attacker.position.position, new Vector2d()));
        ss.create();

        CollisionShape collisionShape = new Rectangle(attacker.position, new Vector2d(24, 24));

        Vector2d swingPos = attacker.targetPos.sub(attacker.position.position, new Vector2d()).normalize().mul(30).add(attacker.position.position);
        for (Behavior b : collisionShape.allTouchingAt(swingPos)) {
            if (b.getOrNull(attacker.target) != null) {
                b.get(Creature.class).health.modify(-(Math.random() + .5) * (5 + charge * 50));
            }
        }
    }

    @Override
    public double cooldown() {
        return .1;
    }

    @Override
    public boolean fireAtMaxCharge() {
        return false;
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
    public boolean payChargeCost(double dt) {
        return attacker.creature.stamina.pay(10 * dt);
    }

    @Override
    public boolean payInitialCost() {
        return attacker.creature.stamina.pay(10);
    }

    @Override
    public double slowdown() {
        return .75;
    }

    public static class SwordSwingBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        @Override
        public void createInner() {
            sprite.sprite = Sprite.load("slash.png");
            lifetime.lifetime = .1;
        }
    }
}
