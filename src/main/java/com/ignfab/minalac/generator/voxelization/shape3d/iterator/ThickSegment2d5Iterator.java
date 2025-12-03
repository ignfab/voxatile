package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickSegment2dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.Segment3d;

/**
 * An iterator over voxels of a {@link Segment3d} with a given horizontal thickness.
 * <p>
 * Beveling could be applied to line ends to connect it in a line string.
 * <p>
 * This iterator is not an exact transposition of 2d iterator to 3d.
 * Actually, on Z axis, it behaves like a "thin" iterator (voxels are not connected along Z-axis).
 */
public class ThickSegment2d5Iterator implements Iterator<Positioned3d> {

    private final Segment3d segment;
    private final Segment2d segment2d;
    private final Iterator<Positioned2d> iterator;

    /**
     * Creates a new iterator on the given segment voxels with ends beveling.
     *
     * @param segment the segment to iterator over
     * @param thickness thickness of the segment in voxels
     * @param startBevelDirection beveling direction at segment start
     * @param endBevelDirection beveling direction at segment end
     */
    public ThickSegment2d5Iterator(Segment3d segment, double thickness, Vector2d startBevelDirection, Vector2d endBevelDirection) {
        this.segment = segment;
        segment2d = segment.to2d();
        iterator = new ThickSegment2dIterator(segment.to2d(), thickness, startBevelDirection, endBevelDirection);
    }

    /**
     * Returns and indexed version of this {@code ThickLine2d5Iterator}.
     * <p>
     * With each voxel position are returned:
     * <ul>
     *   <li>distance to nearest point in segment's line (could be negative depending on which side);
     *   <li>index of nearest point in segment's line (0 is segment start, segment length is segment end, could be outside segment);
     * </ul>
     *
     * @return indexed version of this {@code ThickLine2d5Iterator}
     */
    public Iterator<IndexedPosition3d> indexed() {
        return Iterators.remap(this, position -> {
            WorldCoords2d pos = position.coords().to2d();
            return new IndexedPosition3d(position, segment2d.nearestPointIndex(pos), segment2d.signedDistanceTo(pos));
        });
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Positioned3d next() {
        Positioned2d position = iterator.next();
        // A linear Z value is simply added to 2d position
        return position.coords().to3d(segment.atIndex(segment2d.nearestPointIndex(position.coords())).z());
    }
}
