package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator returning voxels of each position on a 3d line.
 *
 * @see Line3d
 */
public class Line3dConnectedIterator implements Iterator<LineVoxel3d> {
    private final Line3d line;
    private int index = 0;
    private WorldCoords3d last = null;
    private LineVoxel3d next = null;

    /**
     * Creates a new iterator on the given line.
     *
     * @param line the line to iterator over.
     */
    public Line3dConnectedIterator(Line3d line) {
        this.line = line;
    }

    @Override
    public boolean hasNext() {
        return index <= line.maxIndex();
    }

    @Override
    public LineVoxel3d next() {
        // If there were a pending position, return it
        if (next != null) {
            LineVoxel3d result = next;
            next = null;
            return result;
        }

        if (index > line.maxIndex())
            throw new NoSuchElementException();

        LineVoxel3d result = new LineVoxel3d(line, index);
        WorldCoords3d pos = result.coords();

        // If we change both x and y, we have to send an extra position
        if (last != null && pos.x() != last.x() && pos.y() != last.y()) {
            // This is the extra position we'll send next time
            next = result;

            // Meanwhile, we return an intermediate position ensuring everything is connected (in 2d)
            if (Math.abs(line.slopeX()) > Math.abs(line.slopeY()))
                last = new WorldCoords3d(last.x() + (int) Math.round(line.slopeX()), last.y(), last.z());
            else
                last = new WorldCoords3d(last.x(), last.y() + (int) Math.round(line.slopeY()), last.z());

            return new LineVoxel3d(line, last, index);
        }

        last = pos;
        index++;
        return result;
    }
}
