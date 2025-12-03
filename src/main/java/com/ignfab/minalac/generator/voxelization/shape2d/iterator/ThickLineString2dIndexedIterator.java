package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * An iterator returning voxels along a {@link LineString2d} with a given thickness.
 * <p>
 * With each voxel position are returned:
 * <ul>
 *   <li>distance to nearest point in linestring (could be negative depending on which side);
 *   <li>index of that nearest point along the whole linestring (from 0 to linestring length);
 * </ul>
 */
public class ThickLineString2dIndexedIterator implements Iterator<IndexedPosition2d> {

    private final ThickLineString2dIterator iterator;
    private double xOffset = 0.0;
    private Segment2d currentSegment = null;

    /**
     * Creates a new line string iterator whith thickness.
     *
     * @param lineString the line string to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString2dIndexedIterator(LineString2d lineString, double thickness) {
        iterator = new ThickLineString2dIterator(lineString, thickness);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IndexedPosition2d next() {
        Positioned2d position = iterator.next();
        Segment2d segment = iterator.getCurrentSegment();

        // This will be used to merge successive lines X-axes into one linestring long X-axis.
        if (segment != currentSegment) {
            if (currentSegment != null)
                xOffset += currentSegment.length();
            currentSegment = segment;
        }

        return new IndexedPosition2d(
             position,
             xOffset + currentSegment.nearestPointIndex(position),
             currentSegment.signedDistanceTo(position)
        );
    }
}
