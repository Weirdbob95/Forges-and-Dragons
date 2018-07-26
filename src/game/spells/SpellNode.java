package game.spells;

import game.Creature;
import game.spells.SpellPosition.CreatureSpellPosition;
import java.util.LinkedList;
import java.util.List;

public abstract class SpellNode {

    public SpellPosition position;
    public SpellPosition goal;

    public abstract void cast();

    public static abstract class SpellEffect extends SpellNode {

        public static class FireDamage extends SpellEffect {

            @Override
            public void cast() {
                if (position instanceof CreatureSpellPosition) {
                    Creature creature = ((CreatureSpellPosition) position).creature;
                    creature.health.modify(-20);
                }
            }
        }
    }

    public static abstract class SpellTarget extends SpellNode {

        public List<SpellNode> castOnHit = new LinkedList();

//        public void hit() {
//            castOnHit.forEach(sn -> sn.cast());
//        }
        public static class SpellArea extends SpellTarget {

            @Override
            public void cast() {

            }
        }
    }
}
