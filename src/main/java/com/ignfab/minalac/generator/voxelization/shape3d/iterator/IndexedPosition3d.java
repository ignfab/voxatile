package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel position (in world) with 2d projected geometry indexes and distances.
 *
 * @param coords Voxel position in world
 * @param index Index of nearest point in 2d projected geometry
 * @param distance Distance to nearest point in 2d projected geometry (could be negative depending on which side)
 */
public record IndexedPosition3d(WorldCoords3d coords, double index, double distance) implements Positioned3d {
    /**
     * Creates an {@code Indexed2dPosition3d} out of a {@link Positioned3d}.
     *
     * @param positioned the positioned object
     * @param index Index of nearest point in 2d projected geometry
     * @param distance Distance to nearest point in 2d projected geometry (could be negative depending on which side)
     */
    public IndexedPosition3d(Positioned3d positioned, double index, double distance) {
        this(positioned.coords(), index, distance);
    }
}
