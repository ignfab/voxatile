package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel is a {@link WorldCoords3d} that can have additional information.
 *
 * @see IndexedVoxel3d
 */
public interface Voxel3d {
    /**
     * The coordinate of this voxel.
     *
     * @return the voxel coordinate.
     */
    WorldCoords3d coords();

    /**
     * Default implementation with only coordinate.
     *
     * @param coords the voxel coordinate.
     */
    record Impl(WorldCoords3d coords) implements Voxel3d {}
}
