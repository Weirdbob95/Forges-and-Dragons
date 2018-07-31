package game.spells;

import static behaviors.Other.onTimer;
import static game.GraphicsEffect.createGraphicsEffect;
import graphics.Graphics;
import java.util.*;
import org.joml.Vector2d;
import static util.MathUtils.unitVector;

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

        public static class SE_FireDamage extends SpellEffect {

            @Override
            public void cast(SpellInstance si) {
                if (si.creature() != null) {
                    si.creature().health.modify(-(4 * Math.pow(si.mana(), .8)));
                }
            }

            @Override
            public double personalCost() {
                return .5;
            }
        }

        public static class SE_Teleport extends SpellEffect {

            @Override
            public void cast(SpellInstance si) {
                if (si.creature() != null) {

                    Vector2d goal = si.goal();
                    if (si.position().distance(goal) > si.mana() * 10) {
                        Vector2d delta = goal.sub(si.position(), new Vector2d());
                        goal = delta.normalize().mul(si.mana() * 10).add(si.position());
                    }
                    Vector2d finalGoal = new Vector2d(goal).add(unitVector(Math.random() * 2 * Math.PI));
                    onTimer(0, () -> si.creature().position.position = finalGoal);
                }
            }

            @Override
            public double personalCost() {
                return 1;
            }
        }
    }

    public static abstract class SpellTarget extends SpellNode {

        private final List<SpellNode> castOnHit = new LinkedList();
        private final List<SpellNode> castOnNot = new LinkedList();

//        public void hit(double mana, List<SpellInstance> l) {
//            double manaRemaining = mana - personalCost();
//            double manaPer = manaRemaining / (castOnHit.size() * l.size());
//            for (SpellInstance lsi : l) {
//                lsi.mana = manaPer;
//                for (SpellNode sn : castOnHit) {
//                    if (sn.minCost() <= manaPer) {
//                        if (sn.castDelay() > 0) {
//                            onTimer(sn.castDelay(), () -> sn.cast(lsi));
//                        } else {
//                            sn.cast(lsi);
//                        }
//                    }
//                }
//            }
//        }
//
//        public void hit(double mana, SpellInstance... l) {
//            hit(mana, Arrays.asList(l));
//        }
        public void hit(SpellInstance si) {
            if (!castOnHit.isEmpty()) {
                SpellInstance nsi = si.setMana((si.mana() - personalCost()) / castOnHit.size());
                for (SpellNode sn : castOnHit) {
                    if (sn.minCost() <= nsi.mana()) {
                        if (sn.castDelay() > 0) {
                            onTimer(sn.castDelay(), () -> sn.cast(nsi));
                        } else {
                            sn.cast(nsi);
                        }
                    }
                }
            }
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

        public void not(SpellInstance si) {
            if (!castOnNot.isEmpty()) {
                SpellInstance nsi = si.setMana((si.mana() - .1) / castOnNot.size());
                createGraphicsEffect(.1, () -> Graphics.drawCircle(si.position(), 20, si.transparentColor()));
                onTimer(.1, () -> {
                    for (SpellNode sn : castOnNot) {
                        if (sn.minCost() <= nsi.mana()) {
                            sn.cast(nsi);
                        }
                    }
                });
            }
        }

        public SpellTarget onHit(SpellNode... sn) {
            castOnHit.addAll(Arrays.asList(sn));
            return this;
        }

        public SpellTarget onNot(SpellNode... sn) {
            castOnNot.addAll(Arrays.asList(sn));
            return this;
        }
    }
}
