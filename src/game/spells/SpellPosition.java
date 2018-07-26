package game.spells;

import game.Creature;
import java.util.function.Supplier;
import org.joml.Vector2d;

public interface SpellPosition extends Supplier<Vector2d> {

    public static class CreatureSpellPosition implements SpellPosition {

        public final Creature creature;

        public CreatureSpellPosition(Creature creature) {
            this.creature = creature;
        }

        @Override
        public Vector2d get() {
            return new Vector2d(creature.position.position);
        }
    }

    public static class StaticSpellPosition implements SpellPosition {

        public final Vector2d position;

        public StaticSpellPosition(Vector2d position) {
            this.position = position;
        }

        @Override
        public Vector2d get() {
            return new Vector2d(position);
        }
    }
}
