package game;

import engine.Behavior;

public class AttackerBehavior extends Behavior {

    public final Creature creature = require(Creature.class);

    public Runnable attackCallback = null;
    public double attackCooldownRemaining = 0;

    public void attack() {
        if (attackCooldownRemaining <= 0) {
            if (attackCallback != null) {
                attackCallback.run();
            }
        }
    }

    @Override
    public void update(double dt) {
        attackCooldownRemaining -= dt;
    }
}
