package chunk;

import chunk.Mesher.Quad;
import static chunk.World.posToChunk;
import engine.Behavior;
import graphics.Camera;
import graphics.SurfaceGroup;
import graphics.SurfaceGroup.Surface;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.MathUtils.*;

public class Chunk extends Behavior {

    public static final int SIDE_LENGTH = 128;
    static final int SIDE_LENGTH_2 = (SIDE_LENGTH + 2);

    public Vector3i pos;
    public OctTree colors;

    private List<List<SurfaceGroup>> levelsOfDetail = new LinkedList();

    public void generate(BlockArray a) {
        colors = new OctTree(a);

        for (int lod = 1; lod <= SIDE_LENGTH; lod *= 2) {
            if (lod > 1) {
                a = a.downsample2();
            }
            int size = SIDE_LENGTH / lod;

            List<SurfaceGroup> surfaceGroups = new LinkedList();
            levelsOfDetail.add(surfaceGroups);
            for (Vector3i dir : ALL_DIRS) {
                SurfaceGroup sg = new SurfaceGroup();
                surfaceGroups.add(sg);
                sg.normal = dir;
                for (int v = 0; v < size; v++) {
                    boolean[][] toDraw = new boolean[size][size];
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            toDraw[i][j] = a.solid(orderComponents(v, dir, i, j)) && !a.solid(orderComponents(v + dirPosNeg(dir), dir, i, j));
                        }
                    }
                    for (Quad q : Mesher.mesh(toDraw)) {
                        Surface s = q.createSurface(v, dir);
                        int lod2 = lod;
                        Stream.of(s.corners).forEach(c -> c.mul(lod2));
                        sg.surfaces.add(s);
                        for (int i = 0; i < q.w; i++) {
                            for (int j = 0; j < q.h; j++) {
                                s.colorTexture[i][j] = a.getColorArray(orderComponents(v, dir, q.x + i, q.y + j));
                            }
                        }
                        for (int i = 0; i < q.w + 1; i++) {
                            for (int j = 0; j < q.h + 1; j++) {
                                boolean[][] blocks = new boolean[2][2];
                                for (int i2 = 0; i2 < 2; i2++) {
                                    for (int j2 = 0; j2 < 2; j2++) {
                                        blocks[i2][j2] = a.solid(orderComponents(v + dirPosNeg(dir), dir, q.x + i + i2 - 1, q.y + j + j2 - 1));
                                    }
                                }
                                s.shadeTexture[i][j] = getAmbientOcculusion(blocks);
                            }
                        }
                    }
                }
                sg.generateData();
            }
        }
    }

    private static float getAmbientOcculusion(boolean[][] a) {
//        if (a[0][0] || a[1][0] || a[0][1] || a[1][1]) {
//            return .75f;
//        }
        if ((a[0][0] && a[1][1]) || (a[0][1] && a[1][0])) {
            return .55f;
        }
        int numSolid = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            for (int j2 = 0; j2 < 2; j2++) {
                if (a[i2][j2]) {
                    numSolid++;
                }
            }
        }
        switch (numSolid) {
            case 2:
                return .7f;
            case 1:
                return .85f;
            default:
                return 1;
        }
    }

    @Override
    public void render() {
        SurfaceGroup.shader.setUniform("worldMatrix", Camera.camera.getWorldMatrix(toVec3d(pos).mul(SIDE_LENGTH)));

        double dist = World.chunkToCenterPos(pos).sub(Camera.camera.position).length();
        int lodVal = Math.min((int) dist / 500, levelsOfDetail.size() - 1);

        SurfaceGroup.shader.setUniform("lod", 1 << lodVal);
        List<SurfaceGroup> surfaceGroups = levelsOfDetail.get(lodVal);

        for (int i = 0; i < 6; i++) {
            surfaceGroups.get(i).init();
            Vector3d dir = toVec3d(ALL_DIRS.get(i));
            if (toVec3d(pos).add(new Vector3d(.5)).sub(dir.mul(.5)).mul(SIDE_LENGTH).sub(Camera.camera.position).dot(dir) < 0) {
                surfaceGroups.get(i).render();
            }
        }
    }

    @Override
    public void update(double dt) {
        if (posToChunk(Camera.camera.position).distance(pos) > World.UNLOAD_DISTANCE) {

        }
    }
}
