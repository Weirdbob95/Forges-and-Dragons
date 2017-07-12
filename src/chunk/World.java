package chunk;

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
import org.joml.Vector2i;
import org.joml.Vector3d;
import static util.MathUtils.*;

public class World extends Behavior {

    public static final int LOAD_DISTANCE = 6;
    public static final int UNLOAD_DISTANCE = 8;

    private static final int NUM_THREADS = 3;
    static final FrustumIntersection VIEW_FRUSTUM = new FrustumIntersection();

    private final ChunkSupplier supplier;
    private final Map<Vector2i, Chunk> chunks = new HashMap();
    private final Set<Vector2i> loading = new HashSet();
    private final Set<Vector2i> loadNext = new HashSet();
    private final ThreadPoolExecutor threadPool;

    public World(ChunkSupplier supplier) {
        this.supplier = supplier;
        threadPool = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        threadPool.allowCoreThreadTimeOut(true);

//            loadChunk(new Vector2i(0, 0));
//        for (int z = (int) -MAX_Z; z < MAX_Z; z++) {
//        }
    }

    public boolean collides(Vector3d min, Vector3d max) {
        for (int x = posToChunk(min).x; x <= posToChunk(max).x; x++) {
            for (int y = posToChunk(min).y; y <= posToChunk(max).y; y++) {
                if (loading.contains(new Vector2i(x, y))) {
                    return true;
                }
                if (!chunks.containsKey(new Vector2i(x, y))) {
                    loadChunk(new Vector2i(x, y));
                    return true;
                }
                Chunk c = chunks.get(new Vector2i(x, y));
                if (c != null && c.colors.solid(min.sub(chunkToPos(new Vector2i(x, y)), new Vector3d()), max.sub(chunkToPos(new Vector2i(x, y)), new Vector3d()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroyInner() {
        threadPool.shutdownNow();
    }

    private void loadChunk(Vector2i v) {
        if (!loading.contains(v)) {
            if (loading.size() < NUM_THREADS) {
                loading.add(v);

                loadNext.remove(v);
                Chunk prev = chunks.get(v);

                threadPool.execute(() -> {
                    int lod = clamp(desiredLOD(v) - 1, 0, 6);

                    BlockColumns blockColumns = supplier.getLOD(v.x, v.y, 1 << lod);
                    Chunk c = new Chunk();
                    c.pos = v;
                    c.generate(blockColumns, lod);
                    onMainThread(() -> {
                        chunks.put(v, c);
                        c.create();

                        for (Vector2i neighbor : ALL_DIRS_2) {
                            if (!chunks.containsKey(v.add(neighbor, new Vector2i()))) {
                                loadNext.add(v.add(neighbor, new Vector2i()));
                            }
                        }
                    });

                    if (prev != null) {
                        prev.destroy();
                    }
                    loading.remove(v);
                });
            }
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

    public static Vector3d chunkToCenterPos(Vector2i pos) {
        return toVec3d(pos).add(new Vector3d(.5)).mul(Chunk.SIDE_LENGTH);
    }

    public static Vector3d chunkToPos(Vector2i pos) {
        return toVec3d(pos).mul(Chunk.SIDE_LENGTH);
    }

    public static Vector2i posToChunk(Vector3d pos) {
        return new Vector2i((int) Math.floor(pos.x / Chunk.SIDE_LENGTH), (int) Math.floor(pos.y / Chunk.SIDE_LENGTH));
    }

    static int desiredLOD(Vector2i pos) {
        return (int) World.chunkToCenterPos(pos).sub(Camera.camera.position).length() / 400;
    }
}
