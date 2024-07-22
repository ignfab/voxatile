package com.ignfab.minalac.generator.utils.shape2d.iterator;

import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 2d line.
 *
 * @see Line2d
 */
public class Line2dIterator implements Iterator<LineVoxel2d> {
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
    public LineVoxel2d next() {
        if (index > line.maxIndex())
            throw new NoSuchElementException();
        LineVoxel2d coords = new LineVoxel2d(line, index);
        index++;
        return coords;
    }
}
