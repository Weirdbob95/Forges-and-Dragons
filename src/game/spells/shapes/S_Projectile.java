package game.spells.shapes;

import behaviors.ColliderBehavior.Rectangle;
import behaviors.SpriteBehavior;
import engine.Behavior;
import static engine.Behavior.track;
import game.Creature;
import game.Twinkle;
import game.spells.SpellInfo;
import graphics.Sprite;
import java.util.Collection;
import org.joml.Vector2d;
import static util.MathUtils.direction;

public class S_Projectile extends SpellShapeMissile {

    private static final Collection<Creature> ALL_CREATURES = track(Creature.class);

    @Override
    public void cast(SpellInfo info, Vector2d goal) {
        spawnMissiles(info, goal, S_ProjectileBehavior.class, 600);
    }

    public static class S_ProjectileBehavior extends Behavior {

        public final MissileBehavior missile = require(MissileBehavior.class);
        public final SpriteBehavior sprite = require(SpriteBehavior.class);

        @Override
        public void createInner() {
            missile.collider.collisionShape = new Rectangle(missile.position, new Vector2d(4, 4));
            missile.lifetime.lifetime = .75;
            missile.homingRate = 5;
            sprite.sprite = Sprite.load("firebolt.png");
            sprite.rotation = direction(missile.velocity.velocity);
            sprite.scale = 2;
        }

        @Override
        public void destroyInner() {
            Twinkle at = new Twinkle();
            at.position.position = new Vector2d(missile.position.position);
            at.animation.sprite.color = missile.info.color();
            at.create();
        }
    }
}
