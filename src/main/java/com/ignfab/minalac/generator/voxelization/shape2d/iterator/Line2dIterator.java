package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearVoxel2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 2d line.
 *
 * @see Line2d
 */
public class Line2dIterator implements Iterator<LinearVoxel2d> {
    private final Line2d line;
    private int index = 0;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public Line2dIterator(Line2d line) {
        this.line = line;
    }

    @Override
    public boolean hasNext() {
        return index <= line.maxIndex();
    }

    @Override
    public LinearVoxel2d next() {
        if (index > line.maxIndex())
            throw new NoSuchElementException();
        LinearVoxel2d coords = new LinearVoxel2d(line, index);
        index++;
        return coords;
    }
}
