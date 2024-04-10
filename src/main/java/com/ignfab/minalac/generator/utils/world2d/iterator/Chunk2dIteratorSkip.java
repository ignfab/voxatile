package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.chunk.ReadableChunk2d;

import java.util.NoSuchElementException;

/**
 * An iterator over the elements of a {@code ReadableChunk2d}.
 * This iterator skips the elements that have a value equals to a certain {@code skipValue}.
 */
public class Chunk2dIteratorSkip implements Chunk2dIterator {
    private ReadableChunk2d chunk;
    private WorldBBox2dIterator bboxIterator;
    private Chunk2dElement current;
    private int skipValue;

    /**
     * Constructs a new {@code Chunk2dIteratorSkip}.
     *
     * @param chunk     the {@code ReadableChunk2d} to iterate over.
     * @param skipValue the value to skip.
     */
    public Chunk2dIteratorSkip(ReadableChunk2d chunk, int skipValue) {
        this.chunk = chunk;
        bboxIterator = chunk.bbox().iterator();
        this.skipValue = skipValue;
        moveOn();
    }

    private void moveOn() {
        WorldCoords2d coords;
        int value;

        try {
            do {
                coords = bboxIterator.next();
                value = chunk.get(coords);
            } while (value == skipValue);

            current = new Chunk2dElement(coords, value);
        } catch (NoSuchElementException e) {
            current = null;
        }
    }

    /**
     * Indicates if there are more elements.
     *
     * @return {@code true} if the iteration has more elements.
     */
    @Override
    public boolean hasNext() {
        return current != null;
    }

    /**
     * Returns the next element.
     *
     * @return the next {@code Chunk2dElement} in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    @Override
    public Chunk2dElement next() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();

        Chunk2dElement result = current;
        moveOn();
        return result;
    }
}
