package game.spells;

import game.Creature;
import game.spells.SpellPosition.CreatureSpellPosition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class SpellInstance {

    private final SpellPosition position;
    private final SpellPosition goal;
    private final double mana;
    private final Vector4d color;
    private final List<SpellPosition> previousPositions;

    public SpellInstance(SpellPosition position, SpellPosition goal, double mana, Vector4d color) {
        this.position = position;
        this.goal = goal;
        this.mana = mana;
        this.color = color;
        this.previousPositions = new LinkedList();
    }

    public SpellInstance(SpellPosition position, SpellPosition goal, double mana, Vector4d color, List<SpellPosition> previousPositions) {
        this.position = position;
        this.goal = goal;
        this.mana = mana;
        this.color = color;
        this.previousPositions = previousPositions;
    }

    public SpellInstance(SpellInstance other) {
        this.position = other.position;
        this.goal = other.goal;
        this.mana = other.mana;
        this.color = other.color;
        this.previousPositions = other.previousPositions;
    }

    public Creature creature() {
        if (position instanceof CreatureSpellPosition) {
            return ((CreatureSpellPosition) position).creature;
        }
        return null;
    }

    public Vector4d color() {
        return new Vector4d(color);
    }

    public Vector2d goal() {
        return new Vector2d(goal.get());
    }

    public SpellPosition goalSP() {
        return goal;
    }

    public SpellInstance gotoPrevPosition(int n) {
        if (previousPositions.size() < n) {
            return gotoPrevPosition(previousPositions.size());
        }
        List<SpellPosition> copy = new ArrayList(previousPositions);
        SpellPosition past = copy.remove(copy.size() - n);
        return new SpellInstance(past, goal, mana, color, copy);
    }

    public double mana() {
        return mana;
    }

    public SpellInstance pay(double cost) {
        if (mana >= cost) {
            return setMana(mana - cost);
        }
        return null;
    }

    public Vector2d position() {
        return new Vector2d(position.get());
    }

    public SpellPosition positionSP() {
        return position;
    }

    public SpellPosition prevPosition(int n) {
        if (previousPositions.size() < n) {
            return prevPosition(previousPositions.size());
        }
        return previousPositions.get(previousPositions.size() - n);
    }

    public SpellInstance setGoal(SpellPosition goal) {
        return new SpellInstance(position, goal, mana, color, previousPositions);
    }

    public SpellInstance setMana(double mana) {
        return new SpellInstance(position, goal, mana, color, previousPositions);
    }

    public SpellInstance setPosition(SpellPosition position) {
        List<SpellPosition> copy = new ArrayList(previousPositions);
        copy.add(this.position);
        return new SpellInstance(position, goal, mana, color, copy);
    }

    public Vector4d transparentColor() {
        return new Vector4d(1, 1, 1, .2).mul(color);
    }
}
