package chunk;

import static chunk.ChunkSupplier.MAX_Z;
import engine.Behavior;
import static engine.Core.onMainThread;
import graphics.Camera;
import graphics.SurfaceGroup;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.MathUtils.*;
import util.Resources;

public class World extends Behavior {

    public static final int LOAD_DISTANCE = 8;
    public static final int UNLOAD_DISTANCE = 30;

    private static final int NUM_THREADS = 6;

    private final ChunkSupplier supplier;
    private final Map<Vector3i, Chunk> chunks = new HashMap();
    private final Set<Vector3i> loadNext = new HashSet();
    private final ThreadPoolExecutor threadPool;

    public World(ChunkSupplier supplier) {
        this.supplier = supplier;
        threadPool = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        threadPool.allowCoreThreadTimeOut(true);
//        if (Chunk.shaderProgram == null) {
//            Chunk.shaderProgram = new ShaderProgram(Resources.loadFileAsString("src/glsl/chunk.vert"), Resources.loadFileAsString("src/glsl/chunk.frag"));
//        }
        if (SurfaceGroup.shader == null) {
            SurfaceGroup.shader = Resources.loadShaderProgram("new_chunk");
        }

        for (int z = (int) -MAX_Z; z < MAX_Z; z++) {
            loadChunk(new Vector3i(0, 0, z));
        }
    }

    @Override
    public void destroyInner() {
        threadPool.shutdownNow();
    }

    public void loadChunk(Vector3i v) {
        loadNext.remove(v);
        Chunk prev = chunks.put(v, null);

        threadPool.execute(() -> {
            int lod = clamp(desiredLOD(v) - 1, 0, 7);

            BlockArray blockArray = supplier.getLOD(v.x, v.y, v.z, 1 << lod);
            if (blockArray != null) {
                Chunk c = new Chunk();
                c.pos = v;
                c.generate(blockArray, lod);
                chunks.put(v, c);
                onMainThread(() -> {
                    c.create();

                    for (Vector3i neighbor : ALL_DIRS) {
                        if (!chunks.containsKey(v.add(neighbor, new Vector3i()))) {
                            loadNext.add(v.add(neighbor, new Vector3i()));
                        }
                    }
                });
            }

            if (prev != null) {
                prev.destroy();
            }
        });
    }

    @Override
    public void update(double dt) {
        if (!chunks.containsKey(posToChunk(Camera.camera.position))) {
            loadChunk(posToChunk(Camera.camera.position));
        }
        Stream.concat(loadNext.stream(), chunks.keySet().stream())
                .filter(v -> !chunks.containsKey(v) || (chunks.get(v) != null && desiredLOD(v) < chunks.get(v).minLOD))
                .min(Comparator.comparingDouble(v -> chunkToCenterPos(v).distance(Camera.camera.position)))
                .filter(v -> chunkToCenterPos(v).distance(Camera.camera.position) < Chunk.SIDE_LENGTH * LOAD_DISTANCE)
                .ifPresent(this::loadChunk);
    }

    public static Vector3d chunkToCenterPos(Vector3i pos) {
        return toVec3d(pos).add(new Vector3d(.5)).mul(Chunk.SIDE_LENGTH);
    }

    public static Vector3d chunkToPos(Vector3i pos) {
        return toVec3d(pos).mul(Chunk.SIDE_LENGTH);
    }

    public static Vector3i posToChunk(Vector3d pos) {
        return new Vector3i((int) Math.floor(pos.x / Chunk.SIDE_LENGTH), (int) Math.floor(pos.y / Chunk.SIDE_LENGTH), (int) Math.floor(pos.z / Chunk.SIDE_LENGTH));
    }

    static int desiredLOD(Vector3i pos) {
        return (int) World.chunkToCenterPos(pos).sub(Camera.camera.position).length() / 500;
    }
}
