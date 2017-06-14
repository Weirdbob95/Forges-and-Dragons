package chunk;

public class FlatChunkSupplier implements ChunkSupplier {

    private final BlockArray flatChunk = new BlockArray();
    private final BlockArray emptyChunk = new BlockArray();

    public FlatChunkSupplier(int height, int color) {
        for (int i = 0; i < Chunk.SIDE_LENGTH; ++i) {
            for (int j = 0; j < Chunk.SIDE_LENGTH; ++j) {
                flatChunk.setColor(i, height, j, color);
            }
        }
    }

    @Override
    public BlockArray get(int x, int y, int z) {
        if (y == 0) {
            return flatChunk;
        } else {
            return emptyChunk;
        }
    }
}
