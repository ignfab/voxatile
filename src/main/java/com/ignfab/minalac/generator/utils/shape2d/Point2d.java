package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.SingletonIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

/**
 * Represents a 2d point in the voxel world.
 *
 * {@code Point2d} does have neither inside nor border voxels (no line information).
 * Its only voxel will be accessible through {@code allVoxels()} iterable.
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

    @Override
    public Iterable<Voxel2d> allVoxels() {
        return () -> new SingletonIterator<>(coords);
    }
}
