package game.attacktypes;

import game.spells.TypeDefinitions.SpellEffectType;
import game.spells.TypeDefinitions.SpellElement;
import game.spells.TypeDefinitions.SpellShapeInitial;
import game.spells.TypeDefinitions.SpellShapeModifier;

public class AT_ChanneledSpell extends SpellAttackType {

    private double timeElapsed;

    public AT_ChanneledSpell(SpellElement element, SpellEffectType effectType, SpellShapeInitial shapeInitial, SpellShapeModifier... shapeModifiers) {
        super(element, effectType, shapeInitial, shapeModifiers);
    }

    @Override
    public void attack() {
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
        if (attacker.creature.mana.pay(20 * dt * (1 + charge))) {
            if (charge > minCharge()) {
                timeElapsed += dt;
                if (timeElapsed > .125) {
                    timeElapsed -= .125;
                    cast(.1 * (1 + charge));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean payInitialCost() {
        return true;
    }

    @Override
    public double maxCharge() {
        return 2;
    }

    @Override
    public double minCharge() {
        return .2;
    }

    @Override
    public double slowdown() {
        return .75;
    }
}
