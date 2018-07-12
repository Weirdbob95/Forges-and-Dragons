package game;

import behaviors.PhysicsBehavior;
import behaviors.PositionBehavior;
import behaviors.SpaceOccupierBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import graphics.Animation;
import org.joml.Vector2d;

public class Monster extends Behavior {

    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);
    public final Creature creature = require(Creature.class);
    public final FourDirAnimation fourDirAnimation = require(FourDirAnimation.class);
    public final SpaceOccupierBehavior spaceOccupier = require(SpaceOccupierBehavior.class);

    @Override
    public void createInner() {
        physics.collider.hitboxSize = new Vector2d(16, 24);
        fourDirAnimation.animation.animation = new Animation("skeleton_anim");
    }

    @Override
    public void update(double dt) {
        Vector2d goalVelocity = Behavior.findRoot(Player.class).position.position.sub(position.position, new Vector2d());
        goalVelocity.normalize();
        goalVelocity.mul(100);

        double acceleration = 20;
        velocity.velocity.lerp(goalVelocity, 1 - Math.exp(acceleration * -dt));
    }
}
