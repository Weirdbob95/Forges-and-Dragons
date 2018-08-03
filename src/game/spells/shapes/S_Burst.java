package game.spells.shapes;

import static engine.Behavior.track;
import game.Creature;
import static game.GraphicsEffect.createGraphicsEffect;
import game.spells.SpellInfo;
import game.spells.TypeDefinitions.SpellShapeModifier;
import graphics.Graphics;
import java.util.Collection;

public class S_Burst extends SpellShapeModifier {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInfo info) {
        if (info.target.targetsCreature) {
            for (Creature c : ALL_CREATURES) {
                if (c.position.position.distance(info.position()) < 200) {
                    hit(info.setTarget(c).multiplyPower(.5));
                }
            }
        } else {
            throw new RuntimeException("Not implemented yet");
        }
        createGraphicsEffect(.2, lt -> Graphics.drawCircle(info.position(), 200 * Math.pow(5 * lt, .7), info.colorTransparent(.2 * info.powerMultiplier)));
    }
}
