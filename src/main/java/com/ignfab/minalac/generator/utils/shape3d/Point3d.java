package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel3d;

import java.util.Iterator;

/**
 * Represents a 3d point in the voxel world.
 * It stores position in milli-voxel precision.
 */
public class Point3d implements Iterable<IndexedVoxel3d> {
    private final WorldCoords3d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point3d(WorldCoords3d coords) {
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
    public Iterator<IndexedVoxel3d> iterator() {
        return new SingletonIterator<>(new IndexedVoxel3d.Impl(coords, 0));
    }
}
