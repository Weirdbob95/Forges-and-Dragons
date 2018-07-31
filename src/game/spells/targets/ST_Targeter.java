package game.spells.targets;

import game.spells.SpellInstance;
import game.spells.SpellNode.SpellTarget;

public class ST_Targeter extends SpellTarget {

    private final int n;

    public ST_Targeter(int n) {
        this.n = n;
    }

    public ST_Targeter() {
        this.n = 1;
    }

    @Override
    public void cast(SpellInstance si) {
        hit(si.setGoal(si.positionSP()).gotoPrevPosition(n));
    }

    @Override
    public double personalCost() {
        return 0;
    }
}
