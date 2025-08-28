package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * An iterator returning voxels of each position on a 2d segment.
 * <p>
 * This iterator follows "thin line algorithm": it gives minimal
 * (and so only diagonally connected) voxels following the line segment.
 *
 * @see Segment2d
 */
public class ThinSegment2dIterator implements Iterator<Positioned2d> {
    private final Segment2d segment;
    private final int maxIndex;
    private final double indexFactor;
    private int index = 0;

    /**
     * Creates a new iterator on the given segment.
     *
     * @param segment the segment to iterator over.
     */
    public ThinSegment2dIterator(Segment2d segment) {
        this.segment = segment;
        maxIndex = Math.max(segment.bbox().sizeX(), segment.bbox().sizeY()) - 1;
        indexFactor = maxIndex > 0 ? segment.length() / maxIndex : 0;
    }

    @Override
    public boolean hasNext() {
        return index <= maxIndex;
    }

    @Override
    public Positioned2d next() {
        if (index > maxIndex)
            throw new NoSuchElementException();
        WorldCoords2d coords = segment.atIndex(index * indexFactor);
        index++;
        return coords;
    }
}
