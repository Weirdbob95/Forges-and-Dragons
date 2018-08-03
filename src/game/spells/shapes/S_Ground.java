package game.spells.shapes;

import game.spells.SpellInfo;
import game.spells.TypeDefinitions.SpellShapeInitial;
import org.joml.Vector2d;

public class S_Ground extends SpellShapeInitial {

    @Override
    public void cast(SpellInfo info, Vector2d goal) {
        if (goal.distance(info.position()) > 500) {
            goal = info.position().add(goal.sub(info.position()).normalize().mul(500));
        }
        hit(info.setTarget(goal));
    }
}
