package game;

import behaviors.PhysicsBehavior;
import behaviors.PositionBehavior;
import behaviors.SpaceOccupierBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import engine.Input;
import graphics.Animation;
import graphics.Camera;
import java.util.List;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static util.MathUtils.direction;

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
        attacker.attackCallback = this::doSwordSwing;
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

        double acceleration = 2000;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));

        if (Input.mouseDown(0)) {
            attacker.attack();
        }
        if (Input.keyJustPressed(GLFW_KEY_1)) {
            attacker.attackCallback = this::doSwordSwing;
        }
        if (Input.keyJustPressed(GLFW_KEY_2)) {
            attacker.attackCallback = this::doBowAttack;
        }

        Camera.camera.position.lerp(position.position, 1 - Math.exp(5 * -dt));
    }

    public void doBowAttack() {
        Arrow a = new Arrow();
        a.position.position = new Vector2d(position.position);
        a.velocity.velocity = Input.mouseWorld().sub(position.position).normalize().mul(1000);
        a.target = Monster.class;
        a.create();

        attacker.attackCooldownRemaining = .4;
    }

    public void doSwordSwing() {
        SwordSwing ss = new SwordSwing();
        ss.position.position = position.position;
        ss.sprite.rotation = direction(Input.mouseWorld().sub(position.position));
        ss.create();

        Vector2d swingPos = Input.mouseWorld().sub(position.position).normalize().mul(30).add(position.position);
        ss.attack((List) physics.collider.allTouchingAt(swingPos));
//        for (ColliderBehavior cb : physics.collider.allTouchingAt(swingPos)) {
//            Monster m = cb.getOrNull(Monster.class);
//            if (m != null) {
//                m.creature.hpCurrent -= 20;
//            }
//        }
        attacker.attackCooldownRemaining = .3;
    }
}
