package chunk;

import static chunk.SimplexNoiseChunkSupplier.MAX_Z;
import engine.Behavior;
import static engine.Core.onMainThread;
import graphics.Camera;
import graphics.SurfaceGroup;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.joml.Vector3d;
import org.joml.Vector3i;
import util.Resources;
import static util.VectorUtils.ALL_DIRS;

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
        chunks.put(v, null);

        threadPool.execute(() -> {
            BlockArray blockArray = supplier.get(v.x, v.y, v.z);
            if (blockArray != null) {
                Chunk c = new Chunk();
                c.pos = v;
                c.generate(blockArray);
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
        });
    }

    @Override
    public void update(double dt) {
        if (!chunks.containsKey(posToChunk(Camera.camera.position))) {
            loadChunk(posToChunk(Camera.camera.position));
        }
        loadNext.stream()
                .min(Comparator.comparingDouble(v -> v.distance(posToChunk(Camera.camera.position))))
                .filter(v -> v.distance(posToChunk(Camera.camera.position)) < LOAD_DISTANCE)
                .ifPresent(this::loadChunk);
    }

    public static Vector3d chunkToPos(Vector3i pos) {
        return new Vector3d(pos.x, pos.y, pos.z).mul(Chunk.SIDE_LENGTH);
    }

    public static Vector3i posToChunk(Vector3d pos) {
        return new Vector3i((int) Math.floor(pos.x / Chunk.SIDE_LENGTH), (int) Math.floor(pos.y / Chunk.SIDE_LENGTH), (int) Math.floor(pos.z / Chunk.SIDE_LENGTH));
    }
}
