package engine;

import behaviors.Other.FPSBehavior;
import static behaviors.Other.onRender;
import static behaviors.Other.onUpdate;
import chunk.Chunk;
import chunk.SimplexNoiseChunkSupplier;
import chunk.World;
import opengl.Camera;
import static opengl.Camera.camera;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class Main {

    public static void main(String[] args) {
        Core.init();

        onUpdate(dt -> {
            if (Input.keyJustPressed(GLFW_KEY_ESCAPE)) {
                Core.stopGame();
            }

            moveCamera(dt);
        });

        onRender(() -> {
            glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (Chunk.shaderProgram != null) {
                Chunk.shaderProgram.setUniform("projectionMatrix", Camera.getProjectionMatrix());
            }
        });

        new FPSBehavior().create();

        new World(new SimplexNoiseChunkSupplier(0)).create();

        Core.run();
    }

    public static boolean sprint;

    public static void moveCamera(double dt) {
        camera.horAngle -= Input.mouseDelta().x / 500;
        camera.vertAngle += Input.mouseDelta().y / 500;

        if (camera.vertAngle > 1.5) {
            camera.vertAngle = 1.5f;
        }
        if (camera.vertAngle < -1.5) {
            camera.vertAngle = -1.5f;
        }

        if (Input.keyJustPressed(GLFW_KEY_LEFT_CONTROL)) {
            sprint = !sprint;
        }

        double cameraSpeed = (sprint ? 100 : 10) * dt;

        if (Input.keyDown(GLFW_KEY_W)) {
            Vector3dc forward = camera.facing();
            Vector3d horizontalForward = forward.mul(cameraSpeed, new Vector3d());
            horizontalForward.z = 0;
            camera.position.add(horizontalForward);
        }
        if (Input.keyDown(GLFW_KEY_S)) {
            Vector3dc forward = camera.facing();
            Vector3d horizontalForward = forward.mul(-cameraSpeed, new Vector3d());
            horizontalForward.z = 0;
            camera.position.add(horizontalForward);
        }
        if (Input.keyDown(GLFW_KEY_A)) {
            Vector3dc forward = camera.facing();
            Vector3d horizontalForward = forward.mul(cameraSpeed, new Vector3d());
            horizontalForward.z = 0;
            horizontalForward.rotateAbout((float) Math.PI / 2, 0, 0, 1);
            camera.position.add(horizontalForward);
        }
        if (Input.keyDown(GLFW_KEY_D)) {
            Vector3dc forward = camera.facing();
            Vector3d horizontalForward = forward.mul(cameraSpeed, new Vector3d());
            horizontalForward.z = 0;
            horizontalForward.rotateAbout((float) -Math.PI / 2, 0, 0, 1);
            camera.position.add(horizontalForward);
        }
        if (Input.keyDown(GLFW_KEY_SPACE)) {
            camera.position.add(0, 0, cameraSpeed);
        }
        if (Input.keyDown(GLFW_KEY_LEFT_SHIFT)) {
            camera.position.add(0, 0, -cameraSpeed);
        }
    }
}
