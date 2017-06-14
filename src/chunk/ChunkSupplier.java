package chunk;

public interface ChunkSupplier {

    /**
     * Returns the chunk at chunk position x, y, and z. Note: the chunk position
     * is not in world coordinates, but rather in chunk coordinates, which can
     * be calculated with `floor(world_coordinate / Chunk.SIDE_LENGTH)`.
     */
    public BlockArray get(int x, int y, int z);
}
