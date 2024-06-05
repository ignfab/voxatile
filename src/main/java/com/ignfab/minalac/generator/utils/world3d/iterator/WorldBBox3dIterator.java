package com.ignfab.minalac.generator.utils.world3d.iterator;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all coordinates in a {@link WorldBBox3d}.
 */
public class WorldBBox3dIterator implements Iterator<WorldCoords3d> {
    private final WorldBBox3d bbox;
    private int x;
    private int y;
    private int z;
    private boolean hasNext;

    /**
     * Constructs a new {@link WorldBBox3dIterator}.
     *
     * @param bbox the {@link WorldBBox3d} to iterate over.
     */
    public WorldBBox3dIterator(WorldBBox3d bbox) {
        this.bbox = bbox;
        x = bbox.getMin().x();
        y = bbox.getMin().y();
        z = bbox.getMin().z();

        if (bbox.getSize().volume() > 0)
            hasNext = true;
    }

    private void moveOn() {
        x++;
        if (x > bbox.getMax().x()) {
            x = bbox.getMin().x();
            y++;
            if (y > bbox.getMax().y()) {
                y = bbox.getMin().y();
                z++;
                if (z > bbox.getMax().z())
                    hasNext = false;
            }
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
     * @return the next {@link WorldCoords3d} in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    public WorldCoords3d next() throws NoSuchElementException {
        if (!hasNext)
            throw new NoSuchElementException();
        WorldCoords3d result = new WorldCoords3d(x, y, z);
        moveOn();
        return result;
    }
}
