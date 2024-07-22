package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldMilliCoords2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;

import java.util.Iterator;

/**
 * Represents a 2d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point2d implements Iterable<IndexedVoxel2d> {
    private final WorldMilliCoords2d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point2d(WorldMilliCoords2d coords) {
        this.coords = coords;
    }

    /**
     * Returns an iterator returning a single voxel for this point.
     * The index in the voxel will always be {@code 0},
     * and is present only for compatibility purpose.
     *
     * @return an iterator for this point.
     */
    @Override
    public Iterator<IndexedVoxel2d> iterator() {
        return new SingletonIterator<>(new IndexedVoxel2d.Impl(coords.toWorldCoords(), 0));
    }
}
