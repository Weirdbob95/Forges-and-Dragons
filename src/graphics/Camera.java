package graphics;

import opengl.Window;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class Camera {

    public static Camera camera = new Camera();

    public Vector2d position = new Vector2d(0, 0);
    public double rotation = 0;
    public double zoom = 1;

    private Matrix4d getViewMatrix() {
        return new Matrix4d()
                .scale(zoom)
                .rotate(rotation, 0, 0, 1)
                .translate(new Vector3d(position.x, position.y, 0));
    }

    public Matrix4d getWorldMatrix(Vector2d position, double rotation, double scaleX, double scaleY) {
        return getViewMatrix()
                .rotate(-rotation, 0, 0, 1)
                .scale(scaleX, scaleY, 1)
                .translate(new Vector3d(position.x, position.y, 0));
    }

    public static Matrix4d getProjectionMatrix() {
        return getProjectionMatrix(Window.WIDTH * -.5, Window.WIDTH * .5, Window.HEIGHT * -.5, Window.HEIGHT * .5);
    }

    private static Matrix4d getProjectionMatrix(double left, double right, double bottom, double top) {
        Matrix4d projectionMatrix = new Matrix4d();
        projectionMatrix.setOrtho2D(left, right, bottom, top);
        return projectionMatrix;
    }
}
