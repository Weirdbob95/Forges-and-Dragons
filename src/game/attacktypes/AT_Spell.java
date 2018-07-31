package game.attacktypes;

import game.AttackType;
import game.spells.SpellInstance;
import game.spells.SpellNode;
import game.spells.SpellPosition.CreatureSpellPosition;
import game.spells.SpellPosition.StaticSpellPosition;
import org.joml.Vector4d;

public class AT_Spell extends AttackType {

    private SpellNode sn;

    public AT_Spell(SpellNode sn) {
        this.sn = sn;
    }

    @Override
    public void attack() {
        SpellInstance si = new SpellInstance(
                new CreatureSpellPosition(attacker.creature),
                new StaticSpellPosition(attacker.targetPos),
                50,
                new Vector4d(1, .2, 0, 1));
        sn.cast(si);
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
