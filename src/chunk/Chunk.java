package chunk;

import chunk.Mesher.Quad;
import static chunk.World.posToChunk;
import engine.Behavior;
import graphics.Camera;
import graphics.SurfaceGroup;
import graphics.SurfaceGroup.Surface;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.VectorUtils.*;

public class Chunk extends Behavior {

    public static final int SIDE_LENGTH = 128;
    static final int SIDE_LENGTH_2 = (SIDE_LENGTH + 2);

    public Vector3i pos;
    public OctTree colors;

    private List<SurfaceGroup> surfaceGroups = new LinkedList();

    public void generate(BlockArray a) {
        colors = new OctTree(a);

        for (Vector3i dir : ALL_DIRS) {
            SurfaceGroup sg = new SurfaceGroup();
            surfaceGroups.add(sg);
            sg.normal = dir;
            for (int v = 0; v < SIDE_LENGTH; v++) {
                boolean[][] toDraw = new boolean[SIDE_LENGTH][SIDE_LENGTH];
                for (int i = 0; i < SIDE_LENGTH; i++) {
                    for (int j = 0; j < SIDE_LENGTH; j++) {
                        toDraw[i][j] = a.isSolid(orderComponents(v, dir, i, j)) && !a.isSolid(orderComponents(v + dirPosNeg(dir), dir, i, j));
                    }
                }
                for (Quad q : Mesher.mesh(toDraw)) {
                    Surface s = q.createSurface(v, dir);
                    sg.surfaces.add(s);
                    for (int i = 0; i < q.w; i++) {
                        for (int j = 0; j < q.h; j++) {
                            s.colorTexture[i][j] = a.getColorArray(orderComponents(v, dir, q.x + i, q.y + j));
                        }
                    }
                }
            }
            sg.generateData();
        }
    }

    @Override
    public void update(double dt) {
        if (posToChunk(Camera.camera.position).distance(pos) > World.UNLOAD_DISTANCE) {

        }
    }

    @Override
    public void render() {
        SurfaceGroup.shader.setUniform("worldMatrix", Camera.camera.getWorldMatrix(toVec3d(pos).mul(SIDE_LENGTH)));

        for (int i = 0; i < 6; i++) {
            surfaceGroups.get(i).init();
            Vector3d dir = toVec3d(ALL_DIRS.get(i));
            if (toVec3d(pos).add(new Vector3d(.5)).sub(dir.mul(.5)).mul(SIDE_LENGTH).sub(Camera.camera.position).dot(dir) < 0) {
                surfaceGroups.get(i).render();
            }
        }
    }
}
