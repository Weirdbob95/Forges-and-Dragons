package chunk;

import chunk.Mesher.Quad;
import static chunk.World.*;
import engine.Behavior;
import graphics.Camera;
import graphics.SurfaceGroup;
import graphics.SurfaceGroup.Surface;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.MathUtils.*;

public class Chunk extends Behavior {

    public static final int SIDE_LENGTH = 128;
    static final int SIDE_LENGTH_2 = (SIDE_LENGTH + 2);

    public Vector2i pos;
    public BlockColumns colors;

    int minLOD;
    private List<List<SurfaceGroup>> levelsOfDetail = new LinkedList();

    public void generate(BlockColumns a, int minLOD) {
        colors = a;
        this.minLOD = minLOD;

        Vector3i min = new Vector3i(0, 0, a.minZ());
        Vector3i del = new Vector3i(a.size - 2, a.size - 2, a.maxZ() + 1 - a.minZ());

        List<SurfaceGroup> surfaceGroups = new LinkedList();
        levelsOfDetail.add(surfaceGroups);

        for (Vector3i dir : ALL_DIRS) {
            SurfaceGroup sg = new SurfaceGroup(1 << minLOD);
            surfaceGroups.add(sg);
            sg.normal = dir;

            for (int v = 0; v < getComponent(del, dir, 2); v++) {
                boolean[][] toDraw = new boolean[getComponent(del, dir, 0)][getComponent(del, dir, 1)];
                for (int i = 0; i < getComponent(del, dir, 0); i++) {
                    for (int j = 0; j < getComponent(del, dir, 1); j++) {
                        toDraw[i][j] = a.solid(orderComponents(v, dir, i, j).add(min)) && !a.solid(orderComponents(v + dirPosNeg(dir), dir, i, j).add(min));
                    }
                }
                for (Quad q : Mesher.mesh(toDraw)) {
                    Surface s = q.createSurface(v, dir);
                    Stream.of(s.corners).forEach(c -> c.add(min).mul(1 << minLOD));
                    sg.surfaces.add(s);
                    for (int i = 0; i < q.w; i++) {
                        for (int j = 0; j < q.h; j++) {
                            s.colorTexture[i][j] = colorToArray(a.getBlock(orderComponents(v, dir, q.x + i, q.y + j).add(min)));
                        }
                    }
                    for (int i = 0; i < q.w + 1; i++) {
                        for (int j = 0; j < q.h + 1; j++) {
                            boolean[][] blocks = new boolean[2][2];
                            for (int i2 = 0; i2 < 2; i2++) {
                                for (int j2 = 0; j2 < 2; j2++) {
                                    blocks[i2][j2] = a.solid(orderComponents(v + dirPosNeg(dir), dir, q.x + i + i2 - 1, q.y + j + j2 - 1).add(min));
                                }
                            }
                            s.shadeTexture[i][j] = getAmbientOcculusion(blocks);
                        }
                    }
                }
            }
        }

        for (int lod = 2; lod < 4; lod *= 2) {
            surfaceGroups = surfaceGroups.stream().map(s -> s.downsample2()).collect(Collectors.toList());
            levelsOfDetail.add(surfaceGroups);
        }

        levelsOfDetail.forEach(l -> l.forEach(s -> s.generateData()));
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

    private boolean intersectsFrustum() {
        return VIEW_FRUSTUM.testAab(Chunk.SIDE_LENGTH * pos.x, Chunk.SIDE_LENGTH * pos.y, colors.minZ(),
                Chunk.SIDE_LENGTH * (pos.x + 1), Chunk.SIDE_LENGTH * (pos.y + 1), colors.maxZ());
    }

    @Override
    public void render() {
        if (intersectsFrustum()) {
            SurfaceGroup.shader.setUniform("worldMatrix", Camera.camera.getWorldMatrix(toVec3d(pos).mul(SIDE_LENGTH)));

            int lod = clamp(desiredLOD(pos), minLOD, minLOD + levelsOfDetail.size() - 1);
            SurfaceGroup.shader.setUniform("lod", 1 << lod);
            List<SurfaceGroup> surfaceGroups = levelsOfDetail.get(lod - minLOD);

            Vector3d min = chunkToPos(pos).add(new Vector3d(0, 0, colors.minZ()));
            Vector3d max = chunkToPos(pos).add(new Vector3d(SIDE_LENGTH, SIDE_LENGTH, colors.maxZ()));

            for (int i = 0; i < 6; i++) {
                surfaceGroups.get(i).init();
                Vector3d dir = toVec3d(ALL_DIRS.get(i));

                if (Camera.camera.position.sub(min, new Vector3d()).dot(dir) > 0 || Camera.camera.position.sub(max, new Vector3d()).dot(dir) > 0) {
                    surfaceGroups.get(i).render();
                }

//                if (toVec3d(pos).add(new Vector3d(.5)).sub(dir.mul(.5)).mul(SIDE_LENGTH).sub(Camera.camera.position).dot(dir) < 0) {
//                    surfaceGroups.get(i).render();
//                }
            }
        }
    }

//    @Override
//    public void update(double dt) {
//        if (posToChunk(Camera.camera.position).distance(pos) > World.UNLOAD_DISTANCE) {
//
//        }
//    }
}
