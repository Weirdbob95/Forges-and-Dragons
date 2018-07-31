package game.spells.targets;

import game.spells.SpellInstance;
import game.spells.SpellNode.SpellTarget;

public class ST_Revert extends SpellTarget {

    private final int n;

    public ST_Revert(int n) {
        this.n = n;
    }

    public ST_Revert() {
        this.n = 1;
    }

    @Override
    public void cast(SpellInstance si) {
        hit(si.setGoal(si.prevPosition(n)));
    }

    @Override
    public double personalCost() {
        return 0;
    }
}
