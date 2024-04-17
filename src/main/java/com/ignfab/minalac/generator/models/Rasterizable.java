package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.chunk.ReadableChunk2d;

/**
 * Objects that can be rasterized into {@code ReadableChunk2d}.
 */
public interface Rasterizable {
    /**
     * Returns rasterized {@code ReadableChunk2d}.
     *
     * @return Rasterized chunk
     */
    ReadableChunk2d getChunk();
}
