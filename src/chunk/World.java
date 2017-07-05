package chunk;

import static chunk.ChunkSupplier.MAX_Z;
import engine.Behavior;
import static engine.Core.onMainThread;
import graphics.Camera;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3i;
import static util.MathUtils.*;

public class World extends Behavior {

    public static final int LOAD_DISTANCE = 6;
    public static final int UNLOAD_DISTANCE = 8;

    private static final int NUM_THREADS = 3;
    private static final FrustumIntersection VIEW_FRUSTUM = new FrustumIntersection();

    private final ChunkSupplier supplier;
    private final Map<Vector3i, Chunk> chunks = new HashMap();
    private final Set<Vector3i> loading = new HashSet();
    private final Set<Vector3i> loadNext = new HashSet();
    private final ThreadPoolExecutor threadPool;

    public World(ChunkSupplier supplier) {
        this.supplier = supplier;
        threadPool = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        threadPool.allowCoreThreadTimeOut(true);

        for (int z = (int) -MAX_Z; z < MAX_Z; z++) {
            loadChunk(new Vector3i(0, 0, z));
        }
    }

    public boolean collides(Vector3d min, Vector3d max) {
        for (int x = posToChunk(min).x; x <= posToChunk(max).x; x++) {
            for (int y = posToChunk(min).y; y <= posToChunk(max).y; y++) {
                for (int z = posToChunk(min).z; z <= posToChunk(max).z; z++) {
                    if (loading.contains(new Vector3i(x, y, z))) {
                        return true;
                    }
                    if (!chunks.containsKey(new Vector3i(x, y, z))) {
                        loadChunk(new Vector3i(x, y, z));
                        return true;
                    }
                    Chunk c = chunks.get(new Vector3i(x, y, z));
                    if (c != null && c.colors.collides(min.sub(chunkToPos(new Vector3i(x, y, z)), new Vector3d()), max.sub(chunkToPos(new Vector3i(x, y, z)), new Vector3d()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void destroyInner() {
        threadPool.shutdownNow();
    }

    private void loadChunk(Vector3i v) {
        if (loading.size() < NUM_THREADS) {
            loading.add(v);

            loadNext.remove(v);
            Chunk prev = chunks.get(v);

            System.out.println(chunks.size());

            threadPool.execute(() -> {
                int lod = clamp(desiredLOD(v) - 1, 0, 6);

                BlockArray blockArray = supplier.getLOD(v.x, v.y, v.z, 1 << lod);
                if (blockArray != null) {
                    Chunk c = new Chunk();
                    c.pos = v;
                    c.generate(blockArray, lod);
                    onMainThread(() -> {
                        chunks.put(v, c);
                        c.create();

                        for (Vector3i neighbor : ALL_DIRS) {
                            if (!chunks.containsKey(v.add(neighbor, new Vector3i()))) {
                                loadNext.add(v.add(neighbor, new Vector3i()));
                            }
                        }
                    });
                } else {
                    onMainThread(() -> {
                        chunks.put(v, null);
                    });
                }

                if (prev != null) {
                    prev.destroy();
                }
                loading.remove(v);
            });
        }
    }

    @Override
    public void render() {
        VIEW_FRUSTUM.set(new Matrix4f(Camera.getProjectionMatrix().mul(Camera.camera.getWorldMatrix(new Vector3d()))));
    }

    @Override
    public void update(double dt) {
        if (loading.size() < NUM_THREADS && Math.random() < .05) {
            if (!chunks.containsKey(posToChunk(Camera.camera.position))) {
                loadChunk(posToChunk(Camera.camera.position));
            }
            Stream.concat(loadNext.stream(), chunks.keySet().stream())
                    .filter(v -> !loading.contains(v))
                    .filter(v -> !chunks.containsKey(v) || (chunks.get(v) != null && desiredLOD(v) < chunks.get(v).minLOD))
                    .min(Comparator.comparingDouble(v -> chunkToCenterPos(v).distance(Camera.camera.position)))
                    .filter(v -> chunkToCenterPos(v).distance(Camera.camera.position) < Chunk.SIDE_LENGTH * LOAD_DISTANCE)
                    .ifPresent(this::loadChunk);
        }
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
        return (int) World.chunkToCenterPos(pos).sub(Camera.camera.position).length() / 400;
    }

    static boolean frustumIntersects(Vector3i pos) {
        return VIEW_FRUSTUM.testAab(Chunk.SIDE_LENGTH * pos.x, Chunk.SIDE_LENGTH * pos.y, Chunk.SIDE_LENGTH * pos.z,
                Chunk.SIDE_LENGTH * (pos.x + 1), Chunk.SIDE_LENGTH * (pos.y + 1), Chunk.SIDE_LENGTH * (pos.z + 1));
    }
}
