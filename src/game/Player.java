package game;

import behaviors.*;
import engine.Behavior;
import engine.Input;
import game.attacktypes.AT_Arrow;
import game.attacktypes.AT_ChanneledSpell;
import game.attacktypes.AT_ChargedSpell;
import game.attacktypes.AT_InstantSpell;
import game.attacktypes.AT_SwordSwing;
import static game.spells.TypeDefinitions.SpellEffectType.DESTRUCTION;
import static game.spells.TypeDefinitions.SpellElement.FIRE;
import game.spells.shapes.S_Burst;
import game.spells.shapes.S_Ground;
import game.spells.shapes.S_Projectile;
import game.spells.shapes.S_Ray;
import game.spells.shapes.S_Rune;
import game.spells.shapes.SpellShapeMissile;
import graphics.Animation;
import graphics.Camera;
import org.joml.Vector2d;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Behavior {

    static {
        track(Player.class);
    }

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);
    public final FourDirAnimation fourDirAnimation = require(FourDirAnimation.class);
    public final AttackerBehavior attacker = require(AttackerBehavior.class);
    public final SpaceOccupierBehavior spaceOccupier = require(SpaceOccupierBehavior.class);

    @Override
    public void createInner() {
        physics.collider.collisionShape = new ColliderBehavior.Rectangle(position, new Vector2d(16, 24));
        fourDirAnimation.animation.animation = new Animation("skeleton_anim");
        fourDirAnimation.directionSupplier = () -> Input.mouseWorld().sub(position.position);
        fourDirAnimation.playAnimSupplier = () -> velocity.velocity.length() > 10;
        attacker.target = Monster.class;
        attacker.setAttackType(new AT_SwordSwing());
    }

    @Override
    public void update(double dt) {
        attacker.creature.health.modify(100);
        attacker.creature.mana.modify(30 * dt);
        attacker.creature.stamina.modify(100);

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
        goalVelocity.mul(attacker.creature.moveSpeed);
        if (Input.keyDown(GLFW_KEY_LEFT_SHIFT) && goalVelocity.length() > 0 && attacker.creature.stamina.pay(40 * dt)) {
            goalVelocity.mul(2);
        }
        double acceleration = 2000;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));

        attacker.targetPos = Input.mouseWorld();
        if (Input.mouseJustPressed(0)) {
            attacker.startAttack();
        }
        if (Input.mouseJustReleased(0)) {
            attacker.attackWhenReady();
        }
        if (Input.keyJustPressed(GLFW_KEY_1)) {
            attacker.setAttackType(new AT_SwordSwing());
        }
        if (Input.keyJustPressed(GLFW_KEY_2)) {
            attacker.setAttackType(new AT_Arrow());
        }

        // Fire bolt
        if (Input.keyJustPressed(GLFW_KEY_3)) {
            attacker.setAttackType(new AT_InstantSpell(FIRE, DESTRUCTION, new S_Projectile()));
        }

        // Scorching ray
        if (Input.keyJustPressed(GLFW_KEY_4)) {
            attacker.setAttackType(new AT_InstantSpell(FIRE, DESTRUCTION, new S_Ray()));
        }

        // Fireball
        if (Input.keyJustPressed(GLFW_KEY_5)) {
            attacker.setAttackType(new AT_ChargedSpell(FIRE, DESTRUCTION, new S_Projectile(), new S_Burst()));
        }

        // Glyph of warding
        if (Input.keyJustPressed(GLFW_KEY_6)) {
            attacker.setAttackType(new AT_ChargedSpell(FIRE, DESTRUCTION, new S_Ground(), new S_Rune(), new S_Burst()));
        }

        // Continuous ray
        if (Input.keyJustPressed(GLFW_KEY_7)) {
            SpellShapeMissile s = new S_Ray();
            s.isHoming = true;
            attacker.setAttackType(new AT_ChanneledSpell(FIRE, DESTRUCTION, s));
        }

        // Why would you even
        if (Input.keyJustPressed(GLFW_KEY_8)) {
            SpellShapeMissile s = new S_Ray();
            s.isHoming = true;
            s.isMultishot = true;
            attacker.setAttackType(new AT_ChanneledSpell(FIRE, DESTRUCTION, s, new S_Burst(), new S_Burst()));
        }

//        if (Input.keyJustPressed(GLFW_KEY_6)) {
//            attacker.setAttackType(new AT_ChanneledSpell(FIRE, DESTRUCTION, new S_Ray()));
//        }
//        if (Input.keyJustPressed(GLFW_KEY_7)) {
//            attacker.setAttackType(new AT_InstantSpell(FIRE, DESTRUCTION, new S_Ray()));
//        }
//        if (Input.keyJustPressed(GLFW_KEY_8)) {
//            S_Projectile p = new S_Projectile();
//            p.isMultishot = true;
//            p.isHoming = true;
//            attacker.setAttackType(new AT_ChanneledSpell(FIRE, DESTRUCTION, p, new S_Burst()));
//        }
//        if (Input.keyJustPressed(GLFW_KEY_9)) {
//            S_Ray p = new S_Ray();
//            p.isMultishot = true;
//            p.isHoming = true;
//            attacker.setAttackType(new AT_ChanneledSpell(FIRE, DESTRUCTION, p));
//        }
        Camera.camera.position.lerp(position.position, 1 - Math.exp(5 * -dt));
    }
}
