package game;

public abstract class AttackType {

    public AttackerBehavior attacker;
    public double charge;

    public abstract void attack();

    public abstract double cooldown();

    public abstract boolean fireAtMaxCharge();

    public abstract boolean payChargeCost(double dt);

    public abstract boolean payInitialCost();

    public abstract double maxCharge();

    public abstract double minCharge();

    public abstract double slowdown();

}
