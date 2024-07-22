package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * Represents a 3d point in the voxel world.
 *
 * {@code Point3d} does have neither inside nor border voxels (no line information).
 * Its only voxel will be accessible through {@code allVoxels()} iterable.
 */
public class Point3d implements Positioned3d, Shape3d {
    private final WorldCoords3d coords;

    /**
     * Creates a new point at the given coordinate.
     *
     * @param coords the coordinate of the point.
     */
    public Point3d(WorldCoords3d coords) {
        this.coords = coords;
    }

    @Override
    public WorldCoords3d coords() {
        return coords;
    }

    @Override
    public Iterable<Positioned3d> allVoxels() {
        return Iterables.iterable(this);
    }

}
