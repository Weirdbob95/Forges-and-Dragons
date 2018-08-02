package game.spells;

import game.Creature;
import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class SpellInfo {

    public final SpellTarget target;
    public final double direction;
    public final double strengthMultiplier;
    public final SpellElement element;
    public final SpellEffectType effectType;

    public SpellInfo(Creature caster, Vector2d goal, SpellElement element, SpellEffectType effectType) {
        this.target = new SpellTarget(caster);
        this.direction = direction(goal.sub(caster.position.position, new Vector2d()));
        this.strengthMultiplier = 1;
        this.element = element;
        this.effectType = effectType;
    }

    public SpellInfo(SpellTarget target, double direction, double strengthMultiplier, SpellElement element, SpellEffectType effectType) {
        this.target = target;
        this.direction = direction;
        this.strengthMultiplier = strengthMultiplier;
        this.element = element;
        this.effectType = effectType;
    }

    public Vector2d position() {
        if (target.targetsCreature) {
            return new Vector2d(target.creature.position.position);
        } else {
            return new Vector2d(target.terrain);
        }
    }

    public SpellInfo setTarget(Creature creature) {
        return new SpellInfo(new SpellTarget(creature), direction, strengthMultiplier, element, effectType);
    }

    public SpellInfo setTarget(Vector2d terrain) {
        return new SpellInfo(new SpellTarget(terrain), direction, strengthMultiplier, element, effectType);
    }

    public static class SpellTarget {

        public final boolean targetsCreature;
        public final Creature creature;
        public final Vector2d terrain;

        public SpellTarget(Creature creature) {
            this.targetsCreature = true;
            this.creature = creature;
            this.terrain = null;
        }

        public SpellTarget(Vector2d terrain) {
            this.targetsCreature = false;
            this.creature = null;
            this.terrain = terrain;
        }
    }
}
