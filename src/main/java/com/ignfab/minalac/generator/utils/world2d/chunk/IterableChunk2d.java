package com.ignfab.minalac.generator.utils.world2d.chunk;

import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIterator;

/**
 * The {@code IterableChunk2d} interface represents an iterable {@link ReadableChunk2d}.
 */
public interface IterableChunk2d extends ReadableChunk2d, Iterable<Chunk2dElement> {
    /**
     * Returns an iterator.
     *
     * @return a {@link com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dIterator} to iterate over the elements.
     */
    Chunk2dIterator iterator();
}