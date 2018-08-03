package game;

import behaviors.PositionBehavior;
import engine.Behavior;
import graphics.Graphics;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class AttackerBehavior extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final Creature creature = require(Creature.class);

    public Class<? extends Behavior> target = null;
    public Vector2d targetPos = null;
    private AttackType attackType = null;

    public boolean charging = false;
    private boolean attackWhenReady = false;
    public double attackCooldownRemaining = 0;

    public void attackWhenReady() {
        if (charging) {
            attackWhenReady = true;
        }
    }

    @Override
    public void render() {
        if (charging) {
            double percFull = attackType.charge / attackType.minCharge();
            Vector4d barColor = new Vector4d(1, 1, 1, 1);
            if (attackType.charge == attackType.maxCharge()) {
                percFull = 1;
                barColor = new Vector4d(1, 0, 0, 1);
            } else if (attackType.charge > attackType.minCharge()) {
                percFull = (attackType.charge - attackType.minCharge()) / (attackType.maxCharge() - attackType.minCharge());
                barColor = new Vector4d(1, 1 - percFull, 0, 1);
            }

            Graphics.drawRectangle(new Vector2d(-20, -20).add(position.position), 0, new Vector2d(40, 5), new Vector4d(0, 0, 0, 1));
            Graphics.drawRectangle(new Vector2d(-20, -20).add(position.position), 0, new Vector2d(40 * percFull, 5), barColor);
            Graphics.drawRectangleOutline(new Vector2d(-20, -20).add(position.position), 0, new Vector2d(40, 5), new Vector4d(0, 0, 0, 1));
        }
    }

    @Override
    public double renderLayer() {
        return 1;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
        attackType.attacker = this;
    }

    public void startAttack() {
        if (attackCooldownRemaining <= 0 && !charging && attackType != null) {
            if (attackType.payInitialCost()) {
                charging = true;
                attackType.charge = 0;
                creature.moveSpeed *= attackType.slowdown();
            }
        }
    }

    public void stopAttack() {
        if (charging) {
            charging = false;
            attackWhenReady = false;
            attackCooldownRemaining = attackType.cooldown();
            creature.moveSpeed /= attackType.slowdown();
        }
    }

    @Override
    public void update(double dt) {
        attackCooldownRemaining -= dt;
        if (charging) {
            if (attackType.charge < attackType.maxCharge()) {
                if (attackType.payChargeCost(dt)) {
                    attackType.charge = Math.min(attackType.charge + dt, attackType.maxCharge());
                }
            } else {
                if (attackType.fireAtMaxCharge()) {
                    attackWhenReady();
                }
                attackType.payChargeCost(dt);
            }
            if (attackWhenReady && attackType.charge >= attackType.minCharge()) {
                attackType.attack();
                stopAttack();
            }
        }
    }
}
