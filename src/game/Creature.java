package game;

import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;

public class Creature extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    public double hpMax;
    public double hpCurrent;

    public double STR;
    public double DEX;
}
