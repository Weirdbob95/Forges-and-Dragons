package game;

import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import engine.Behavior;
import graphics.Sprite;
import java.util.Collection;

public class SwordSwing extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

    public Player player;

    public void attack(Collection<Behavior> targets) {
        for (Behavior b : targets) {
            Monster m = b.getOrNull(Monster.class);
            if (m != null) {
                m.creature.hpCurrent -= 20;
            }
        }
    }

    @Override
    public void createInner() {
        sprite.sprite = Sprite.load("slash.png");
        sprite.scale = 1;
        lifetime.lifetime = .1;
    }
}
