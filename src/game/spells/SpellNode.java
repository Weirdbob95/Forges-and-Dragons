package game.spells;

import static behaviors.Other.onTimer;
import game.Creature;
import game.spells.SpellPosition.CreatureSpellPosition;
import java.util.*;

public abstract class SpellNode {

    public abstract void cast(SpellInstance si);

    public double castDelay() {
        return 0;
    }

    public double minCost() {
        return personalCost();
    }

    public abstract double personalCost();

    public static abstract class SpellEffect extends SpellNode {

        public static class FireDamage extends SpellEffect {

            @Override
            public void cast(SpellInstance si) {
                if (si.position instanceof CreatureSpellPosition) {
                    Creature creature = ((CreatureSpellPosition) si.position).creature;
                    creature.health.modify(-(4 * Math.pow(si.mana, .8)));
                }
            }

            @Override
            public double personalCost() {
                return .5;
            }
        }
    }

    public static abstract class SpellTarget extends SpellNode {

        private final List<SpellNode> castOnHit = new LinkedList();

        public void hit(double mana, List<SpellInstance> l) {
            castOnHit.sort(Comparator.comparingDouble(sn -> sn.minCost()));
            double manaRemaining = mana - personalCost();
            double manaPer = manaRemaining / (castOnHit.size() * l.size());
            for (SpellInstance lsi : l) {
                lsi.mana = manaPer;
                for (SpellNode sn : castOnHit) {
                    if (sn.minCost() <= manaPer) {
                        if (sn.castDelay() > 0) {
                            onTimer(sn.castDelay(), () -> sn.cast(lsi));
                        } else {
                            sn.cast(lsi);
                        }
                    }
                }
            }
        }

        public void hit(double mana, SpellInstance... l) {
            hit(mana, Arrays.asList(l));
        }

        @Override
        public double minCost() {
            Map<SpellNode, Double> costToReach = new HashMap();
            costToReach.put(this, personalCost());
            PriorityQueue<SpellNode> toCheck = new PriorityQueue(Comparator.comparingDouble(costToReach::get));
            toCheck.add(this);
            while (!toCheck.isEmpty()) {
                SpellNode next = toCheck.poll();
                if (!(next instanceof SpellTarget)) {
                    return costToReach.get(next);
                }
                SpellTarget st = (SpellTarget) next;
                for (SpellNode sn : st.castOnHit) {
                    double newCost = costToReach.get(st) + sn.personalCost();
                    if (!costToReach.containsKey(sn) || costToReach.get(sn) > newCost) {
                        costToReach.put(sn, newCost);
                        toCheck.remove(sn);
                        toCheck.add(sn);
                    }
                }
            }
            return -1;
        }

        public SpellTarget onHit(SpellNode... sn) {
            castOnHit.addAll(Arrays.asList(sn));
            return this;
        }
    }
}
