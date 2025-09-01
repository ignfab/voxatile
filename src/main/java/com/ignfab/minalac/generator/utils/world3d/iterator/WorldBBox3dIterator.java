package com.ignfab.minalac.generator.utils.world3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

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
        if (bbox.isEmpty()) {
            hasNext = false;
        } else {
            x = bbox.minX();
            y = bbox.minY();
            z = bbox.minZ();
            hasNext = true;
        }
    }

    private void moveOn() {
        x++;
        if (x > bbox.maxX()) {
            x = bbox.minX();
            y++;
            if (y > bbox.maxY()) {
                y = bbox.minY();
                z++;
                if (z > bbox.maxZ())
                    hasNext = false;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public WorldCoords3d next() throws NoSuchElementException {
        if (!hasNext)
            throw new NoSuchElementException();
        WorldCoords3d result = new WorldCoords3d(x, y, z);
        moveOn();
        return result;
    }
}
