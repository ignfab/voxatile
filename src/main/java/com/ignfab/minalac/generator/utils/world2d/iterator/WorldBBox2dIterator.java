package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all coordinates in a {@code WorldBBox2d}.
 */
public class WorldBBox2dIterator implements Iterator<WorldCoords2d> {
    private WorldBBox2d bbox;
    private int x, y;
    private boolean hasNext;

    /**
     * Constructs a new {@code WorldBBox2dIterator}.
     *
     * @param bbox the {@code WorldBBox2d} to iterate over.
     */
    public WorldBBox2dIterator(WorldBBox2d bbox) {
        this.bbox = bbox;
        x = bbox.getMin().x();
        y = bbox.getMin().y();

        if (bbox.getSize().area() > 0)
            hasNext = true;
    }

    private void moveOn() {
        x++;
        if (x > bbox.getMax().x()) {
            x = bbox.getMin().x();
            y++;
            if (y > bbox.getMax().y())
                hasNext = false;
        }
    }

    /**
     * Indicates if there are more elements.
     *
     * @return {@code true} if the iteration has more elements.
     */
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * Returns the next element.
     *
     * @return the next {@code WorldCoords2d} in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    public WorldCoords2d next() throws NoSuchElementException {
        if (!hasNext)
            throw new NoSuchElementException();
        WorldCoords2d result = new WorldCoords2d(x, y);
        moveOn();
        return result;
    }
}
