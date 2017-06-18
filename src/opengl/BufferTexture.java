package opengl;

import engine.Activatable;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.glTexBuffer;

public class BufferTexture implements Activatable {

    public final BufferObject bo;
    public final Texture t;
    private final int type;

    public BufferTexture(int texNum, int type, float[] data) {
        this.type = type;
        bo = new BufferObject(GL_TEXTURE_BUFFER, data);
        t = new Texture(texNum, GL_TEXTURE_BUFFER);
        bo.deactivate();
    }

    @Override
    public void activate() {
        t.activate();
        glTexBuffer(GL_TEXTURE_BUFFER, type, bo.bufferObject);
    }

    @Override
    public void deactivate() {
        t.deactivate();
    }

    public void sendToUniform(ShaderProgram shader, String name) {
        activate();
        shader.setUniform(name, t.texNum);
    }
}
