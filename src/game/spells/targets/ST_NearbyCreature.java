package game.spells.targets;

import static engine.Behavior.track;
import game.Creature;
import static game.GraphicsEffect.createGraphicsEffect;
import game.spells.SpellInstance;
import game.spells.SpellNode.SpellTarget;
import game.spells.SpellPosition.CreatureSpellPosition;
import graphics.Graphics;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ST_NearbyCreature extends SpellTarget {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInstance si) {
        List<Creature> potentialTargets = new LinkedList();
        for (Creature c : ALL_CREATURES) {
            if (c.position.position.distance(si.position()) < 150) {
                if (si.creature() != null && si.creature() != c) {
                    potentialTargets.add(c);
                }
            }
        }
        if (potentialTargets.size() > 0) {
            SpellInstance newSI = si.setPosition(new CreatureSpellPosition(potentialTargets.get((int) (Math.random() * potentialTargets.size()))));
            hit(newSI);
            createGraphicsEffect(.2, () -> Graphics.drawLine(si.position(), newSI.position(), si.color()));
        } else {
            not(si);
        }
    }

    @Override
    public double castDelay() {
        return .1;
    }

    @Override
    public double personalCost() {
        return 1;
    }
}
