package graphics;

import de.matthiasmann.twl.utils.PNGDecoder;
import static engine.Activatable.using;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import opengl.BufferObject;
import opengl.ShaderProgram;
import opengl.Texture;
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
import util.Resources;

public class Sprite {

    private static final Map<String, Sprite> spriteCache = new HashMap();

    public static Sprite load(String fileName) {
        if (!spriteCache.containsKey(fileName)) {
            Sprite s = new Sprite(fileName);
            spriteCache.put(fileName, s);
        }
        return spriteCache.get(fileName);
    }

    private static final float vertices[] = {
        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // top right
        0.5f, -0.5f, 0.0f, 1.0f, 1.0f, // bottom right
        -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // bottom left
        -0.5f, 0.5f, 0.0f, 0.0f, 0.0f // top left
    };
    private static final int indices[] = {
        0, 1, 3, // first Triangle
        1, 2, 3 // second Triangle
    };

    static final ShaderProgram spriteShader = Resources.loadShaderProgram("sprite");

    private static final VertexArrayObject spriteVAO = VertexArrayObject.createVAO(() -> {
        BufferObject vbo = new BufferObject(GL_ARRAY_BUFFER, vertices);
        BufferObject ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER, indices);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(1);
    });

    Texture texture;
    int width;
    int height;

    private Sprite(String fileName) {
        try {
            texture = new Texture("sprites/" + fileName);
            PNGDecoder decoder = new PNGDecoder(new FileInputStream("sprites/" + fileName));
            width = decoder.getWidth();
            height = decoder.getHeight();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void draw(Vector2d position, double rotation, double scale, Vector4d color) {
        spriteShader.setUniform("projectionMatrix", Camera.getProjectionMatrix());
        spriteShader.setUniform("modelViewMatrix", Camera.camera.getWorldMatrix(position, rotation, scale * width, scale * height));
        spriteShader.setUniform("color", color);
        using(Arrays.asList(texture, spriteShader, spriteVAO), () -> {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        });
    }
}
