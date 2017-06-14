package opengl;

import chunk.Chunk;
import static chunk.Chunk.SIDE_LENGTH;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import engine.Activatable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture implements Activatable {

    private int texture;
    private int type;

    public Texture(int type) {
        texture = glGenTextures();
        this.type = type;
        activate();
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glGenerateMipmap(type);
        deactivate();
    }

    public Texture(String fileName) {
        texture = loadTexture(fileName);
        type = GL_TEXTURE_2D;
    }

    @Override
    public void activate() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(type, texture);
    }

    @Override
    public void deactivate() {
        glBindTexture(type, 0);
    }

    public int getID() {
        return texture;
    }

    private static int loadTexture(String fileName) {
        try {
            // Load Texture file
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(fileName));

            // Load texture contents into a byte buffer
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
            buf.flip();

            // Create a new OpenGL texture
            int textureId = glGenTextures();
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, textureId);

            // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,
                    GL_RGBA, GL_UNSIGNED_BYTE, buf);
            // Generate Mip Map
            glGenerateMipmap(GL_TEXTURE_2D);
            return textureId;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Generates a chunk texture
    public Texture(Chunk chunk) {
        texture = glGenTextures();
        type = GL_TEXTURE_2D;

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, texture);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        float[] colors = new float[SIDE_LENGTH * SIDE_LENGTH * SIDE_LENGTH * 3];

        for (int x = 0; x < SIDE_LENGTH; x++) {
            for (int y = 0; y < SIDE_LENGTH; y++) {
                for (int z = 0; z < SIDE_LENGTH; z++) {
                    int col = chunk.colors.get(x, y, z);
                    if (col != 0) {
                        for (int j = 2; j >= 0; j--) {
                            colors[3 * (x * SIDE_LENGTH * SIDE_LENGTH + y * SIDE_LENGTH + z) + j] = (col % 256) / 255.0f;
                            col /= 256;
                        }
                    }
                }
            }
        }

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SIDE_LENGTH * SIDE_LENGTH, SIDE_LENGTH, 0, GL_RGB, GL_FLOAT, colors);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
    }
}
