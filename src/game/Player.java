package game;

import behaviors.PositionBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import engine.Input;
import graphics.Camera;
import graphics.Sprite;
import opengl.Window;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class Player extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final Creature creature = require(Creature.class);

    @Override
    public void createInner() {
        creature.sprite.sprite = new Sprite("rock.png");
    }

    @Override
    public void update(double dt) {
        Vector2d goalVelocity = new Vector2d();
        if (Input.keyDown(GLFW_KEY_W)) {
            goalVelocity.y += 1;
        }
        if (Input.keyDown(GLFW_KEY_A)) {
            goalVelocity.x -= 1;
        }
        if (Input.keyDown(GLFW_KEY_S)) {
            goalVelocity.y -= 1;
        }
        if (Input.keyDown(GLFW_KEY_D)) {
            goalVelocity.x += 1;
        }
        if (goalVelocity.length() > 1) {
            goalVelocity.normalize();
        }
        goalVelocity.mul(200);

        double acceleration = 20;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));

        Vector2d mouseWorld = Input.mouse().sub(Window.WIDTH / 2, Window.HEIGHT / 2, new Vector2d()).sub(Camera.camera.position, new Vector2d());
        creature.sprite.rotation = mouseWorld.sub(position.position, new Vector2d()).angle(new Vector2d(1, 0));
    }
}
