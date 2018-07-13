package graphics;

import static engine.Activatable.using;
import static graphics.Sprite.spriteShader;
import java.util.Arrays;
import java.util.List;
import opengl.BufferObject;
import opengl.VertexArrayObject;
import org.joml.Vector2d;
import org.joml.Vector4d;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class RepeatedSprite {

    private final Sprite sprite;
    private final int num;
    private final VertexArrayObject vao;

    public RepeatedSprite(Sprite sprite, List<Vector2d> positions) {
        this.sprite = sprite;
        num = positions.size();
        float[] vertices = new float[20 * num];
        int[] indices = new int[6 * num];
        for (int i = 0; i < num; i++) {
            float x = (float) positions.get(i).x;
            float y = (float) positions.get(i).y;
            System.arraycopy(new float[]{
                x + 0.5f, y + 0.5f, 0.0f, 1.0f, 0.0f, // top right
                x + 0.5f, y - 0.5f, 0.0f, 1.0f, 1.0f, // bottom right
                x - 0.5f, y - 0.5f, 0.0f, 0.0f, 1.0f, // bottom left
                x - 0.5f, y + 0.5f, 0.0f, 0.0f, 0.0f // top left
            }, 0, vertices, 20 * i, 20);
            System.arraycopy(new int[]{
                4 * i, 4 * i + 1, 4 * i + 3, // first Triangle
                4 * i + 1, 4 * i + 2, 4 * i + 3 // second Triangle
            }, 0, indices, 6 * i, 6);
        }

        vao = VertexArrayObject.createVAO(() -> {
            BufferObject vbo = new BufferObject(GL_ARRAY_BUFFER, vertices);
            BufferObject ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER, indices);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
            glEnableVertexAttribArray(1);
        });
    }

    public void draw(Vector2d position, double rotation, double scale, Vector4d color) {
        spriteShader.setUniform("projectionMatrix", Camera.getProjectionMatrix());
        spriteShader.setUniform("modelViewMatrix", Camera.camera.getWorldMatrix(position, rotation, scale * sprite.width, scale * sprite.height));
        spriteShader.setUniform("color", color);
        using(Arrays.asList(sprite.texture, spriteShader, vao), () -> {
            glDrawElements(GL_TRIANGLES, 6 * num, GL_UNSIGNED_INT, 0);
        });
    }
}
