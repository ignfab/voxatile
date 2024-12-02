package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 3d line.
 *
 * @see Line3d
 */
public class Line2dConnectedIterator implements Iterator<LineVoxel2d> {
    private final Line2d line;
    private int index = 0;
    private WorldCoords2d last = null;
    private LineVoxel2d next = null;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public Line2dConnectedIterator(Line2d line) {
        this.line = line;
    }

    @Override
    public boolean hasNext() {
        return index <= line.maxIndex();
    }

    @Override
    public LineVoxel2d next() {
        // If there were a pending position, return it
        if (next != null) {
            LineVoxel2d result = next;
            next = null;
            return result;
        }

        if (index > line.maxIndex())
            throw new NoSuchElementException();

        LineVoxel2d result = new LineVoxel2d(line, index);
        WorldCoords2d pos = result.coords();

        // If we change both x and y, we have to send an extra position
        if (last != null && pos.x() != last.x() && pos.y() != last.y()) {
            // This is the extra position we'll send next time
            next = result;

            // Meanwhile, we return an intermediate position ensuring everything is connected (in 2d)
            if (Math.abs(line.slopeX()) > Math.abs(line.slopeY()))
                last = new WorldCoords2d(last.x() + (int) Math.round(line.slopeX()), last.y());
            else
                last = new WorldCoords2d(last.x(), last.y() + (int) Math.round(line.slopeY()));

            return new LineVoxel2d(line, last, index);
        }

        last = pos;
        index++;
        return result;
    }
}
