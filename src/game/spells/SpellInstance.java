package game.spells;

public class SpellInstance {

    public SpellPosition position;
    public SpellPosition goal;
    public double mana;

    public SpellInstance(SpellPosition position, SpellPosition goal, double mana) {
        this.position = position;
        this.goal = goal;
        this.mana = mana;
    }

    public SpellInstance(SpellInstance other) {
        this.position = other.position;
        this.goal = other.goal;
        this.mana = other.mana;
    }
}
