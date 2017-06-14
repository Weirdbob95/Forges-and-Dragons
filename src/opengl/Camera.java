package opengl;

import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Camera {

    public static Camera camera = new Camera();

    public Vector3d position = new Vector3d(0, 0, 0);
    public double horAngle, vertAngle;
    public Vector3d up = new Vector3d(0, 0, 1);

    private Matrix4d getViewMatrix() {
        return new Matrix4d()
                .rotate(vertAngle - Math.PI / 2, new Vector3d(1, 0, 0))
                .rotate(Math.PI / 2 - horAngle, new Vector3d(0, 0, 1))
                .translate(position.mul(-1, new Vector3d()));

        // Why am I adding/subtracting doubles from the angles? Idk, but it works.
    }

    public Matrix4d getWorldMatrix(Vector3d translate) {
        return getViewMatrix().translate(translate);
    }

    public static Matrix4d getProjectionMatrix(double fov, double width, double height, double zNear, double zFar) {
        double aspectRatio = width / height;
        Matrix4d projectionMatrix = new Matrix4d();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Vector3d facing() {
        return new Vector3d(Math.cos(vertAngle) * Math.cos(horAngle), Math.cos(vertAngle) * Math.sin(horAngle), Math.sin(vertAngle));
    }
}
