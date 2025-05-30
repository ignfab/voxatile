package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Represents a 3d point in the voxel world.
 * <p>
 * {@code Point3d} does have neither inside nor border voxels (no line information).
 * Its only voxel will be accessible through {@code allVoxels()} iterable.
 * @param coords the coordinate of the point.
 */
public record Point3d(WorldCoords3d coords) implements Positioned3d, Shape3d {
    @Override
    public Iterable<Positioned3d> allVoxels() {
        return Iterables.iterable(this);
    }
}
