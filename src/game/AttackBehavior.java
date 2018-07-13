package game;

import engine.Behavior;
import java.util.function.Consumer;

public class AttackBehavior extends Behavior {

    public Class<? extends Behavior> target = null;
    public double damage = 0;
    public Consumer<Creature> onHitCallback = c -> c.health.modify(-damage);

    public void hit(Behavior target) {
        if (onHitCallback != null) {
            onHitCallback.accept(target.get(Creature.class));
        }
    }
}
