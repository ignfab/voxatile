package com.ignfab.minalac.generator.voxelization.shape2d;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * Represents a 2d point in the voxel world.
 * <p>
 * {@code Point2d} does have neither inside nor border voxels (no line information).
 * Its only voxel will be accessible through {@code allVoxels()} iterable.
 * @param coords the coordinate of the point.
 */
public record Point2d(WorldCoords2d coords) implements Positioned2d, Shape2d {
    @Override
    public Iterable<Positioned2d> allVoxels() {
        return Iterables.iterable(this);
    }
}
