package game;

import static behaviors.Other.onTimer;
import behaviors.PositionBehavior;
import engine.Behavior;
import game.attacks.Arrow;
import game.attacks.Firebolt;
import game.attacks.SwordSwing;
import java.util.function.Consumer;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class AttackerBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final Creature creature = require(Creature.class);

    public Class<? extends Behavior> target = null;
    public Consumer<Vector2d> attackCallback = this::doSwordSwingAttack;
    public double attackCooldownRemaining = 0;

    public void attack(Vector2d targetPos) {
        if (attackCooldownRemaining <= 0) {
            if (attackCallback != null) {
                attackCallback.accept(new Vector2d(targetPos));
            }
        }
    }

    public void doBowAttack(Vector2d targetPos) {
        if (creature.stamina.pay(10)) {
            attackCooldownRemaining = 10 / (10 + creature.DEX);

            onTimer(attackCooldownRemaining / 2, () -> {
                Arrow a = new Arrow();
                a.position.position = new Vector2d(position.position);
                a.velocity.velocity = targetPos.sub(position.position).normalize().mul(1000);
                a.attack.target = target;
                a.attack.damage = (Math.random() + .5) * (10 + creature.STR);
                a.create();
            });

            creature.moveSpeed /= 2;
            onTimer(attackCooldownRemaining, () -> creature.moveSpeed *= 2);
        }
    }

    public void doFireboltAttack(Vector2d targetPos) {
        if (creature.mana.pay(20)) {
            attackCooldownRemaining = .5;

            Firebolt f = new Firebolt();
            f.position.position = new Vector2d(position.position);
            f.velocity.velocity = targetPos.sub(position.position).normalize().mul(600);
            f.attack.target = target;
            f.attack.damage = (Math.random() + .5) * (10 + creature.POW) * 2;
            f.create();
        }
    }

    public void doSwordSwingAttack(Vector2d targetPos) {
        if (creature.stamina.pay(10)) {
            attackCooldownRemaining = 10 / (10 + creature.DEX);

            SwordSwing ss = new SwordSwing();
            ss.position.position = position.position;
            ss.sprite.rotation = direction(targetPos.sub(position.position));
            ss.lifetime.lifetime = Math.min(.1, attackCooldownRemaining / 2);
            ss.attack.target = target;
            ss.attack.damage = (Math.random() + .5) * (10 + creature.STR);
            ss.create();

            creature.moveSpeed /= 2;
            onTimer(attackCooldownRemaining / 2, () -> creature.moveSpeed *= 2);
        }
    }

    @Override
    public void update(double dt) {
        attackCooldownRemaining -= dt;
    }
}
