package game;

import behaviors.PositionBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import graphics.Graphics;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static util.MathUtils.clamp;

public class Creature extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);

    public double hpMax = 100;
    public double hpCurrent = 100;
    public double mpMax = 100;
    public double mpCurrent = 100;
    public double spMax = 100;
    public double spCurrent = 100;

    public double STR = 10;
    public double AGI = 10;
    public double DEX = 10;
    public double RES = 10;
    public double INT = 10;
    public double WIS = 10;
    public double CHA = 10;
    public double LUK = 10;

    public void die() {
        getRoot().destroy();
    }

    @Override
    public void render() {
        double healthBarPerc = clamp(hpCurrent / hpMax, 0, 1);
        Graphics.drawRectangle(new Vector2d(-20, 20).add(position.position), 0, new Vector2d(40, 5), new Vector4d(1, 0, 0, 1));
        Graphics.drawRectangle(new Vector2d(-20, 20).add(position.position), 0, new Vector2d(40 * healthBarPerc, 5), new Vector4d(0, 1, 0, 1));
        Graphics.drawRectangleOutline(new Vector2d(-20, 20).add(position.position), 0, new Vector2d(40, 5), new Vector4d(0, 0, 0, 1));
    }

    @Override
    public void update(double dt) {
        if (hpCurrent <= 1e-9) {
            die();
        }
    }
}
