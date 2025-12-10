package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

public class ConnectedLine2dIterator implements Iterator<Positioned2d> {
    private final Segment2d segment;
    private final int maxIndex;
    private final double indexFactor;
    private final boolean connect;

    private int index;
    private WorldCoords2d last;

    /**
     * Creates a new iterator on the given line.
     *
     * @param segment the segment to iterator over.
     * @param connect whether to connect or not voxels (false = thin line)
     */
    public ConnectedLine2dIterator(Segment2d segment, boolean connect) {
        this.segment = segment;
        this.connect = connect;

        maxIndex = Math.max(segment.bbox().sizeX(), segment.bbox().sizeY()) - 1;
        indexFactor = maxIndex > 0 ? segment.length() / maxIndex : 0;
        index = 0;
        last = segment.atIndex(0);
    }

    @Override
    public boolean hasNext() {
        return index <= maxIndex && indexFactor != 0.0;
    }

    @Override
    public Positioned2d next() {
        if (index > maxIndex)
            throw new NoSuchElementException();
        WorldCoords2d coords = segment.atIndex(index * indexFactor);

        if (!connect) {
            index ++;
            return coords;
        }

        int diff = Math.abs(coords.x() - last.x()) + Math.abs(coords.y() - last.y());

        if (diff > 1)
            // TODO: choose better between x and y according to line relative pos
            coords = new WorldCoords2d(coords.x(), last.y());
        else
            index ++;

        last = coords;
        return coords;
    }
}
