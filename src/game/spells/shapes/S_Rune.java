package game.spells.shapes;

import behaviors.ColliderBehavior;
import behaviors.LifetimeBehavior;
import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import engine.Behavior;
import game.Creature;
import game.spells.SpellInfo;
import game.spells.TypeDefinitions.SpellShapeModifier;
import graphics.Sprite;
import org.joml.Vector2d;

public class S_Rune extends SpellShapeModifier {

    @Override
    public void cast(SpellInfo info) {
        S_RuneBehavior spb = new S_RuneBehavior();
        spb.position.position = info.position();
        spb.rune = this;
        spb.info = info;
        spb.create();
    }

    private static class S_RuneBehavior extends Behavior {

        public final PositionBehavior position = require(PositionBehavior.class);
        public final ColliderBehavior collider = require(ColliderBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);
        public final LifetimeBehavior lifetime = require(LifetimeBehavior.class);

        public S_Rune rune;
        public SpellInfo info;

        @Override
        public void createInner() {
            collider.collisionShape = new ColliderBehavior.Rectangle(position, new Vector2d(16, 16));
            sprite.sprite = Sprite.load("rune.png");
            sprite.color = info.colorTransparent(.2 * info.powerMultiplier);
            sprite.scale = 2;
            lifetime.lifetime = 30;
        }

        @Override
        public void update(double dt) {
            if (lifetime.lifetime < 29) {
                Creature c = collider.collisionShape.findTouching(Creature.class);
                if (c != null) {
                    rune.hit(info.setTarget(c));
                    destroy();
                }
            }
        }
    }
}
