package game.spells;

import game.Creature;
import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import org.joml.Vector2d;

public abstract class EffectDefinitions {

    public static void hitCreature(SpellElement element, SpellEffectType effectType, Creature creature, double powerMultiplier) {
        switch (element) {
            case FIRE:
                switch (effectType) {
                    case DESTRUCTION:
                        creature.health.modify(-20 * powerMultiplier);
                        return;
                }
        }
        throw new RuntimeException("Unknown element/effectType combination");
    }

    public static void hitTerrain(SpellElement element, SpellEffectType effectType, Vector2d terrain, double powerMultiplier) {
        throw new RuntimeException("Unknown element/effectType combination");
    }
}
