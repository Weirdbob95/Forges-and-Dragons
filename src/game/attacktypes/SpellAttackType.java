package game.attacktypes;

import game.AttackType;
import game.spells.SpellInfo;
import game.spells.TypeDefinitions;
import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import game.spells.TypeDefinitions.SpellShapeInitial;
import game.spells.TypeDefinitions.SpellShapeModifier;

public abstract class SpellAttackType extends AttackType {

    private final SpellElement element;
    private final SpellEffectType effectType;
    private final SpellShapeInitial shapeInitial;

    public SpellAttackType(SpellElement element, SpellEffectType effectType, SpellShapeInitial shapeInitial, SpellShapeModifier... shapeModifiers) {
        this.shapeInitial = shapeInitial;
        this.element = element;
        this.effectType = effectType;
        if (shapeModifiers.length == 0) {
            shapeInitial.onHit(new TypeDefinitions.SpellEffectFinal());
        } else {
            shapeInitial.onHit(shapeModifiers[0]);
            for (int i = 0; i < shapeModifiers.length - 1; i++) {
                shapeModifiers[i].onHit(shapeModifiers[i + 1]);
            }
            shapeModifiers[shapeModifiers.length - 1].onHit(new TypeDefinitions.SpellEffectFinal());
        }
    }

    public void cast(double powerMultiplier) {
        SpellInfo info = new SpellInfo(attacker.creature, attacker.targetPos, powerMultiplier, element, effectType);
        shapeInitial.cast(info, attacker.targetPos);
    }
}
