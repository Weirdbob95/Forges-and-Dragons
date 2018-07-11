package game;

import behaviors.PhysicsBehavior;
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
import static util.MathUtils.direction;

public class Player extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);
    public final Creature creature = require(Creature.class);

    @Override
    public void createInner() {
        physics.collider.hitboxSize = new Vector2d(24, 24);
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

        Vector2d mouseWorld = Input.mouse().sub(Window.WIDTH / 2, Window.HEIGHT / 2, new Vector2d()).mul(1, -1).sub(Camera.camera.position);
        creature.sprite.rotation = direction(mouseWorld.sub(position.position, new Vector2d()));

        if (Input.mouseJustPressed(0)) {
            Arrow a = new Arrow();
            a.position.position = new Vector2d(position.position);
            a.velocity.velocity = mouseWorld.sub(position.position, new Vector2d()).normalize().mul(1000);
            a.create();
        }
    }
}
