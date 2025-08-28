package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

/**
 * An iterator returning voxels of each position on a 2d line.
 *
 * This iterator follows "thin line algorithm": it gives minimal
 * (and so only diagonally connected) voxels following the line.
 *
 * @see Line2d
 */
public class ThinLine2dIterator implements Iterator<Positioned2d> {
    private final Line2d line;
    private final int maxIndex;
    private final double indexFactor;
    private int index = 0;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public ThinLine2dIterator(Line2d line) {
        this.line = line;
        maxIndex = Math.max(line.bbox().sizeX(), line.bbox().sizeY()) - 1;
        if (maxIndex > 0)
            indexFactor = line.length() / maxIndex;
        else
            indexFactor = 0;
    }

    @Override
    public boolean hasNext() {
        return index <= maxIndex;
    }

    @Override
    public Positioned2d next() {
        if (index > maxIndex)
            throw new NoSuchElementException();
        WorldCoords2d coords = line.atIndex(index * indexFactor);
        index++;
        return coords;
    }
}
