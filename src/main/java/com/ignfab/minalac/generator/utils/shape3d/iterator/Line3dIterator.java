package com.ignfab.minalac.generator.utils.shape3d.iterator;

import com.ignfab.minalac.generator.utils.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 3d line.
 *
 * @see Line3d
 */
public class Line3dIterator implements Iterator<LineVoxel3d> {
    private final Line3d line;
    private int t = 0;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public Line3dIterator(Line3d line) {
        this.line = line;
    }

    @Override
    public boolean hasNext() {
        return t <= line.maxIndex();
    }

    @Override
    public LineVoxel3d next() {
        if (t > line.maxIndex())
            throw new NoSuchElementException();
        LineVoxel3d coords = new LineVoxel3d(line.atIndex(t), line, t);
        t++;
        return coords;
    }
}
