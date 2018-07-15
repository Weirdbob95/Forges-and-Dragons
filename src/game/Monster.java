package game;

import behaviors.ColliderBehavior.Rectangle;
import behaviors.PhysicsBehavior;
import behaviors.PositionBehavior;
import behaviors.SpaceOccupierBehavior;
import behaviors.SpriteBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import static engine.Behavior.track;
import game.attacktypes.AT_Arrow;
import game.attacktypes.AT_Firebolt;
import game.attacktypes.AT_SwordSwing;
import graphics.Animation;
import java.util.Collection;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class Monster extends Behavior {

    private static final Collection<Player> ALL_PLAYERS = track(Player.class);

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);
    public final SpriteBehavior sprite = require(SpriteBehavior.class);
    public final FourDirAnimation fourDirAnimation = require(FourDirAnimation.class);
    public final AttackerBehavior attacker = require(AttackerBehavior.class);
    public final SpaceOccupierBehavior spaceOccupier = require(SpaceOccupierBehavior.class);

    public double attackRange = 0;

    @Override
    public void createInner() {
        physics.collider.collisionShape = new Rectangle(position, new Vector2d(16, 24));
        fourDirAnimation.animation.animation = new Animation("skeleton_anim");
        fourDirAnimation.directionSupplier = () -> attacker.targetPos.sub(position.position, new Vector2d());
        fourDirAnimation.playAnimSupplier = () -> velocity.velocity.length() > 10;
        attacker.target = Player.class;

        double r = Math.random();
        if (r < .4) {
            attacker.setAttackType(new AT_SwordSwing());
            sprite.color = new Vector4d(1, .9, .9, 1);
            attackRange = 100;
        } else if (r < .8) {
            attacker.setAttackType(new AT_Arrow());
            sprite.color = new Vector4d(.9, 1, .9, 1);
            attackRange = 500;
        } else {
            attacker.setAttackType(new AT_Firebolt());
            sprite.color = new Vector4d(.9, .9, 1, 1);
            attackRange = 500;
        }
    }

    @Override
    public void update(double dt) {
        Vector2d goalVelocity;

        Player player = ALL_PLAYERS.stream().findAny().orElse(null);
        if (player != null) {
            goalVelocity = player.position.position.sub(position.position, new Vector2d());
            goalVelocity.normalize();
            goalVelocity.mul(attacker.creature.moveSpeed);
            if (position.position.distance(player.position.position) < attackRange * .4) {
                goalVelocity.mul(-1);
            } else if (position.position.distance(player.position.position) < attackRange * .6) {
                goalVelocity.mul(0);
            }
            attacker.targetPos = new Vector2d(player.position.position);

            if (position.position.distance(player.position.position) < attackRange) {
                attacker.startAttack();
                attacker.attackWhenReady();
            }
        } else {
            goalVelocity = new Vector2d();
        }

        double acceleration = 20;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));
    }
}
