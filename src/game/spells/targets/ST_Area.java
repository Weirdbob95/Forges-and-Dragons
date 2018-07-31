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

public class ST_Area extends SpellTarget {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInstance si) {
        List<SpellInstance> targets = new LinkedList();
        for (Creature c : ALL_CREATURES) {
            if (c.position.position.distance(si.position()) < 200) {
                targets.add(si.setPosition(new CreatureSpellPosition(c)).setMana(si.mana() / 3));
            }
        }
        if (!targets.isEmpty()) {
            targets.forEach(this::hit);
        } else {
            not(si);
        }
        createGraphicsEffect(.2, () -> Graphics.drawCircle(si.position(), 200, si.transparentColor()));
    }

    @Override
    public double minCost() {
        return super.minCost() * 3;
    }

    @Override
    public double personalCost() {
        return 5;
    }
}
