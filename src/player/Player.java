package player;

import behaviors.AccelerationBehavior;
import behaviors.PhysicsBehavior;
import behaviors.PositionBehavior;
import behaviors.VelocityBehavior;
import engine.Behavior;
import engine.Input;
import static graphics.Camera.camera;
import org.joml.Vector3d;
import static org.lwjgl.glfw.GLFW.*;

public class Player extends Behavior {

    public final AccelerationBehavior acceleration = require(AccelerationBehavior.class);
    public final PositionBehavior position = require(PositionBehavior.class);
    public final VelocityBehavior velocity = require(VelocityBehavior.class);
    public final PhysicsBehavior physics = require(PhysicsBehavior.class);

    public boolean sprint;

    public Player() {
        acceleration.acceleration = new Vector3d(0, 0, -20);
        physics.hitboxSize = new Vector3d(.7, .7, 1.2);

        System.out.println(getSubBehaviors());
    }

    @Override
    public void update(double dt) {

        Vector3d desCamPos = position.position.add(new Vector3d(0, 0, 1), new Vector3d());
//        if (camera.position.distance(desCamPos) > 10 * dt) {
//            camera.position.lerp(desCamPos, Math.max(10 * dt / camera.position.distance(desCamPos), 1 - Math.pow(.1, dt)));
//        } else {
//            camera.position = desCamPos;
//        }
        camera.position.lerp(desCamPos, .3);
//        camera.position.lerp(, 1 - Math.pow(.00001, dt));

        // Look around
        camera.horAngle -= Input.mouseDelta().x / 500;
        camera.vertAngle += Input.mouseDelta().y / 500;

        if (camera.vertAngle > 1.5) {
            camera.vertAngle = 1.5f;
        }
        if (camera.vertAngle < -1.5) {
            camera.vertAngle = -1.5f;
        }

        // Move
        if (Input.keyJustPressed(GLFW_KEY_LEFT_CONTROL)) {
            sprint = !sprint;
        }
        double speed = sprint ? 30 : 8;

        Vector3d forwards = camera.facing();
        forwards.z = 0;
        forwards.normalize();
        Vector3d sideways = camera.up.cross(forwards, new Vector3d());

        Vector3d idealVel = new Vector3d();
        if (Input.keyDown(GLFW_KEY_W)) {
            idealVel.add(forwards);
        }
        if (Input.keyDown(GLFW_KEY_A)) {
            idealVel.add(sideways);
        }
        if (Input.keyDown(GLFW_KEY_S)) {
            idealVel.sub(forwards);
        }
        if (Input.keyDown(GLFW_KEY_D)) {
            idealVel.sub(sideways);
        }
        if (idealVel.lengthSquared() > 0) {
            idealVel.normalize().mul(speed);
        }
        idealVel.z = velocity.velocity.z;

        velocity.velocity.lerp(idealVel, 1 - Math.pow(.005, dt));

        // Jump
        if (Input.keyDown(GLFW_KEY_SPACE)) {
            if (physics.onGround || sprint) {
                velocity.velocity.z = speed * 1.5;
            }
        }
    }

//    @Override
//    public double updateLayer() {
//        return 6;
//    }
}
