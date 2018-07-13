package game;

import behaviors.PhysicsBehavior;
import behaviors.PositionBehavior;
import behaviors.SpaceOccupierBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import engine.Input;
import graphics.Animation;
import graphics.Camera;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class Player extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);
    public final FourDirAnimation fourDirAnimation = require(FourDirAnimation.class);
    public final AttackerBehavior attacker = require(AttackerBehavior.class);
    public final SpaceOccupierBehavior spaceOccupier = require(SpaceOccupierBehavior.class);

    @Override
    public void createInner() {
        physics.collider.hitboxSize = new Vector2d(24, 24);
        fourDirAnimation.animation.animation = new Animation("skeleton_anim");
        fourDirAnimation.directionSupplier = () -> Input.mouseWorld().sub(position.position);
        fourDirAnimation.playAnimSupplier = () -> velocity.velocity.length() > 10;
        attacker.target = Monster.class;
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
        if (Input.keyDown(GLFW_KEY_LEFT_SHIFT) && goalVelocity.length() > 0 && attacker.creature.stamina.pay(40 * dt)) {
            goalVelocity.mul(1.5);
        }
        double acceleration = 2000;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));

        if (Input.mouseDown(0)) {
            attacker.attack(Input.mouseWorld());
        }
        if (Input.keyJustPressed(GLFW_KEY_1)) {
            attacker.attackCallback = attacker::doSwordSwingAttack;
        }
        if (Input.keyJustPressed(GLFW_KEY_2)) {
            attacker.attackCallback = attacker::doBowAttack;
        }
        if (Input.keyJustPressed(GLFW_KEY_3)) {
            attacker.attackCallback = attacker::doFireboltAttack;
        }

        Camera.camera.position.lerp(position.position, 1 - Math.exp(5 * -dt));
    }
}
