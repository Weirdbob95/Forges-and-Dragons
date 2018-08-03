package game.attacktypes;

import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import game.spells.TypeDefinitions.SpellShapeInitial;
import game.spells.TypeDefinitions.SpellShapeModifier;

public class AT_ChargedSpell extends SpellAttackType {

    public AT_ChargedSpell(SpellElement element, SpellEffectType effectType, SpellShapeInitial shapeInitial, SpellShapeModifier... shapeModifiers) {
        super(element, effectType, shapeInitial, shapeModifiers);
    }

    @Override
    public void attack() {
        cast(1 + 1.33 * (charge - minCharge()));
    }

    @Override
    public double cooldown() {
        return .3;
    }

    @Override
    public boolean fireAtMaxCharge() {
        return false;
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
        return 2;
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
