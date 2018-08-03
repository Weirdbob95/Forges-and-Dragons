package game.attacktypes;

import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import game.spells.TypeDefinitions.SpellShapeInitial;
import game.spells.TypeDefinitions.SpellShapeModifier;

public class AT_InstantSpell extends SpellAttackType {

    public AT_InstantSpell(SpellElement element, SpellEffectType effectType, SpellShapeInitial shapeInitial, SpellShapeModifier... shapeModifiers) {
        super(element, effectType, shapeInitial, shapeModifiers);
    }

    @Override
    public void attack() {
        cast(1);
    }

    @Override
    public double cooldown() {
        return .5;
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
        return .05;
    }

    @Override
    public double minCharge() {
        return .05;
    }

    @Override
    public double slowdown() {
        return .5;
    }
}
