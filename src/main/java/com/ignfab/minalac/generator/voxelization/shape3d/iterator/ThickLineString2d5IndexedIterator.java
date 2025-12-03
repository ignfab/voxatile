package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineString3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Segment3d;

/**
 * An iterator over voxels of a {@link LineString3d} with thickness.
 * <p>
 * This iterator is not an exact transposition of 2d iterator to 3d.
 * Actually, on Z axis, it behaves like a "thin" iterator (voxels are not connected along Z-axis).
 * <p>
 * With each voxel position are returned:
 * <ul>
 *   <li>distance to nearest point in 2d projected linestring (could be negative depending on which side);
 *   <li>index of nearest point along the whole 2d projected linestring (from 0 to linestring length);
 * </ul>
 */
public class ThickLineString2d5IndexedIterator implements Iterator<IndexedPosition3d> {
    private final ThickLineString2d5Iterator iterator;

    private double xOffset = 0.0;
    private Segment2d currentSegment2d = null;
    private Segment3d currentSegment3d = null;

    /**
     * Creates a new line string iterator with thickness.
     *
     * @param lineString the lineString to iterator over.
     * @param thickness thickness of the line in voxels.
     */
    public ThickLineString2d5IndexedIterator(LineString3d lineString, double thickness) {
        iterator = new ThickLineString2d5Iterator(lineString, thickness);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IndexedPosition3d next() {
        Positioned3d position = iterator.next();
        WorldCoords2d coords = position.coords().to2d();

        Segment3d segment = iterator.getCurrentSegment();
        if (currentSegment3d != segment) {
            if (currentSegment2d != null)
                xOffset += currentSegment2d.length();
            currentSegment3d = segment;
            currentSegment2d = segment.to2d();
        }

        return new IndexedPosition3d(
            position,
            xOffset + currentSegment2d.nearestPointIndex(coords),
            currentSegment2d.signedDistanceTo(coords)
        );
    }
}
