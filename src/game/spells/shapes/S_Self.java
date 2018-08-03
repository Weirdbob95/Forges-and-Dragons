package game.spells.shapes;

import game.spells.SpellInfo;
import game.spells.TypeDefinitions.SpellShapeInitial;
import org.joml.Vector2d;

public class S_Self extends SpellShapeInitial {

    @Override
    public void cast(SpellInfo info, Vector2d goal) {
        hit(info);
    }
}
