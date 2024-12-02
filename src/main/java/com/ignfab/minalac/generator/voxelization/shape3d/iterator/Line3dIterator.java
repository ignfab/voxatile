package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LinearVoxel3d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 3d line.
 * Some extra information is given for linear and other renderers.
 *
 * @see Line3d
 */
public class Line3dIterator implements Iterator<LinearVoxel3d> {
    private final Line3d line;
    private final Vector3d slope;
    private int index = 0;
    private WorldCoords3d current;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     * @param next next line if any.
     */
    public Line3dIterator(Line3d line, Line3d next) {
        this.line = line;
        slope = (next == null) ? line.slope() : next.slope();
        this.current = line.start();
    }

    @Override
    public boolean hasNext() {
        return index <= line.maxIndex();
    }

    @Override
    public LinearVoxel3d next() {
        if (index > line.maxIndex())
            throw new NoSuchElementException();

        LinearVoxel3d result;
        WorldCoords3d next = line.atIndex(index + 1);

        if (index == line.maxIndex())
            result = new LinearVoxel3d(current, line.slope(), current, slope, index);
        else
            result = new LinearVoxel3d(current, line.slope(), next, line.slope(), index);

        index++;
        current = next;

        return result;
    }
}
