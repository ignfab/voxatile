package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;

/**
 * An iterator returning voxels of each position on a 3d line.
 *
 * @see Line3d
 */
public class Line3dIterator implements Iterator<Positioned3d> {
    private final Line3d line;
    private final int maxIndex;
    private final double indexFactor;
    private int index = 0;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public Line3dIterator(Line3d line) {
        this.line = line;
        maxIndex = Math.max(Math.max(line.bbox().sizeX(), line.bbox().sizeY()), line.bbox().sizeZ()) - 1;
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
    public Positioned3d next() {
        if (index > maxIndex)
            throw new NoSuchElementException();
        WorldCoords3d coords = line.atIndex(index * indexFactor);
        index++;
        return coords;
    }
}
