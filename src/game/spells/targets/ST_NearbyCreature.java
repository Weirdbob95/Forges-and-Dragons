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
import org.joml.Vector4d;

public class ST_NearbyCreature extends SpellTarget {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInstance si) {
        List<Creature> potentialTargets = new LinkedList();
        for (Creature c : ALL_CREATURES) {
            if (c.position.position.distance(si.position.get()) < 150) {
                if (!(si.position instanceof CreatureSpellPosition && ((CreatureSpellPosition) si.position).creature == c)) {
                    potentialTargets.add(c);
                }
            }
        }
        if (potentialTargets.size() > 0) {
            SpellInstance newSI = new SpellInstance(si);
            newSI.position = new CreatureSpellPosition(potentialTargets.get((int) (Math.random() * potentialTargets.size())));
            hit(si.mana, newSI);
            createGraphicsEffect(.2, () -> Graphics.drawLine(si.position.get(), newSI.position.get(), new Vector4d(1, .2, 0, 1)));
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
