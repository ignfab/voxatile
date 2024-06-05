package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.ReadableChunk2d;

import java.util.NoSuchElementException;

/**
 * An iterator over all the elements of a {@code ReadableChunk2d}.
 */
public class Chunk2dIteratorAll implements Chunk2dIterator {

    private final ReadableChunk2d chunk;
    private final WorldBBox2dIterator bboxIterator;

    /**
     * Constructs a new {@code Chunk2dIteratorAll}.
     *
     * @param chunk the {@code ReadableChunk2d} to iterate over.
     */
    public Chunk2dIteratorAll(ReadableChunk2d chunk) {
        this.chunk = chunk;
        bboxIterator = chunk.bbox().iterator();
    }

    /**
     * Indicates if there are more elements.
     *
     * @return {@code true} if the iteration has more elements.
     */
    @Override
    public boolean hasNext() {
        return bboxIterator.hasNext();
    }

    /**
     * Returns the next element.
     *
     * @return the next {@code Chunk2dElement} in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    @Override
    public Chunk2dElement next() throws NoSuchElementException {
        WorldCoords2d current = bboxIterator.next();
        return new Chunk2dElement(current, chunk.get(current));
    }
}
