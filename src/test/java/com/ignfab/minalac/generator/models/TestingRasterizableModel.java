package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.chunk.ReadableChunk2d;

/**
 * A simple Rasterizable Model implementation for test purposes.
 * It just stores a given chunk and give it back as return value of {@code getChunk}.
 */
public class TestingRasterizableModel extends Model implements Rasterizable {
    private ReadableChunk2d chunk;

    /**
     * Constructs a new {@code TestingRasterizableModel}.
     *
     * @param chunk Chunk to be returned by {@code getChunk}
     */
    public TestingRasterizableModel(ReadableChunk2d chunk) {
        super();
        this.chunk = chunk;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadableChunk2d getChunk() {
        return chunk;
    }
}
