package com.ignfab.minalac.generator.utils.shape2d.iterator;

import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 2d line.
 *
 * @see Line2d
 */
public class Line2dIterator implements Iterator<IndexedVoxel2d> {
    private final Line2d line;
    private int t = 0;

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
        return t <= line.maxIndex();
    }

    @Override
    public IndexedVoxel2d next() {
        if (t > line.maxIndex())
            throw new NoSuchElementException();
        IndexedVoxel2d coords = new IndexedVoxel2d.Impl(line.atIndex(t).toWorldCoords(), t);
        t++;
        return coords;
    }
}
