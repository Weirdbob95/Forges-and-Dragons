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

public class ST_Area extends SpellTarget {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInstance si) {
        List<SpellInstance> targets = new LinkedList();
        for (Creature c : ALL_CREATURES) {
            if (c.position.position.distance(si.position.get()) < 200) {
                SpellInstance newSI = new SpellInstance(si);
                newSI.position = new CreatureSpellPosition(c);
                targets.add(newSI);
            }
        }
        hit(si.mana, targets);
        createGraphicsEffect(.2, () -> Graphics.drawCircle(si.position.get(), 200, new Vector4d(1, .2, 0, .2)));
    }

    @Override
    public double personalCost() {
        return 5;
    }
}
