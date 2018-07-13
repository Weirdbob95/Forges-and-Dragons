package game.stats;

import graphics.Graphics;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static util.MathUtils.clamp;

public class StatBar {

    private double max;
    private double current;
    public double regen;

    private Vector4d fullColor;
    private Vector4d emptyColor;

    public StatBar(double max) {
        this.max = max;
        current = max;
        regen = 0;
    }

    public Vector4d currentColor() {
        return emptyColor.lerp(fullColor, percFull(), new Vector4d());
    }

    public void draw(Vector2d position) {
        Graphics.drawRectangle(new Vector2d(-20, 0).add(position), 0, new Vector2d(40, 5), new Vector4d(0, 0, 0, 1));
        Graphics.drawRectangle(new Vector2d(-20, 0).add(position), 0, new Vector2d(40 * percFull(), 5), currentColor());
        Graphics.drawRectangleOutline(new Vector2d(-20, 0).add(position), 0, new Vector2d(40, 5), new Vector4d(0, 0, 0, 1));
    }

    public boolean isEmpty() {
        return percFull() < 1e-9;
    }

    public void modify(double amt) {
        current = clamp(current + amt, 0, max);
    }

    public boolean pay(double amt) {
        if (current >= amt) {
            modify(-amt);
            return true;
        } else {
            return false;
        }
    }

    public double percFull() {
        return current / max;
    }

    public void setColors(Vector4d fullColor, Vector4d emptyColor) {
        this.fullColor = fullColor;
        this.emptyColor = emptyColor;
    }

    public void update(double dt) {
        modify(regen * dt);
    }
}
