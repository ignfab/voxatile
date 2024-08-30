package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Collections;

/**
 * Represents a 2d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point2d implements Shape2d {
    private final WorldCoords2d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point2d(WorldCoords2d coords) {
        this.coords = coords;
    }

    /**
     * Returns an iterable returning a single voxel for this point.
     * The index in the voxel will always be {@code 0},
     * and is present only for compatibility purpose.
     *
     * @return an iterable for this point.
     */
    @Override
    public Iterable<IndexedVoxel2d> borderVoxels() {
        return () -> new SingletonIterator<>(new IndexedVoxel2d.Impl(coords, 0));
    }

    @Override
    public Iterable<Voxel2d> insideVoxels() {
        return Collections::emptyIterator;
    }
}
