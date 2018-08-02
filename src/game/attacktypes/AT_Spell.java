package game.attacktypes;

import game.AttackType;
import game.spells.SpellInfo;
import game.spells.TypeDefinitions;
import game.spells.TypeDefinitions.SpellCastingType;
import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import game.spells.TypeDefinitions.SpellShapeInitial;
import game.spells.TypeDefinitions.SpellShapeModifier;

public class AT_Spell extends AttackType {

    private final SpellCastingType castingType;
    private final SpellElement element;
    private final SpellEffectType effectType;
    private final SpellShapeInitial shapeInitial;

    public AT_Spell(SpellCastingType castingType, SpellElement element, SpellEffectType effectType, SpellShapeInitial shapeInitial, SpellShapeModifier... shapeModifiers) {
        this.castingType = castingType;
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

    @Override
    public void attack() {
        SpellInfo info = new SpellInfo(attacker.creature, attacker.targetPos, element, effectType);
        shapeInitial.cast(info, attacker.targetPos);
    }

    @Override
    public double cooldown() {
        return .3;
    }

    @Override
    public boolean fireAtMaxCharge() {
        return true;
    }

    @Override
    public boolean payChargeCost(double dt) {
        return true;
    }

    @Override
    public boolean payInitialCost() {
        return attacker.creature.mana.pay(20);
    }

    @Override
    public double maxCharge() {
        return .5;
    }

    @Override
    public double minCharge() {
        return .5;
    }

    @Override
    public double slowdown() {
        return .25;
    }
}
