package game;

import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import game.stats.StatBar;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class Creature extends Behavior {

    static {
        track(Creature.class);
    }

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    public StatBar health = new StatBar(100);
    public StatBar mana = new StatBar(100);
    public StatBar stamina = new StatBar(100);

//    public double STR = 10; // Attack damage
//    public double DEX = 10; // Attack speed
//    public double AGI = 10; // Movement speed
//    public double CON = 10; // Health and stamina
//    public double INT = 10; // Mana capacity
//    public double WIS = 10; // Mana regen
//    public double POW = 10; // Magic power
//    public double RES = 10; // Magic resilience
    public double moveSpeed = 150;

    @Override
    public void createInner() {
        health.setColors(new Vector4d(.2, 1, 0, 1), new Vector4d(1, 0, 0, 1));

        mana.setColors(new Vector4d(0, .6, 1, 1), new Vector4d(0, .5, 1, 1));
        mana.regen = 2;

        stamina.setColors(new Vector4d(1, .8, 0, 1), new Vector4d(.5, .4, 0, 1));
        stamina.regen = 10;
    }

    public void die() {
        getRoot().destroy();
    }

    @Override
    public void render() {
        health.draw(new Vector2d(0, 30).add(position.position));
        mana.draw(new Vector2d(0, 25).add(position.position));
        stamina.draw(new Vector2d(0, 20).add(position.position));
    }

    @Override
    public void update(double dt) {
        health.update(dt);
        mana.update(dt);
        stamina.update(dt);
        if (health.isEmpty()) {
            die();
        }
    }
}
