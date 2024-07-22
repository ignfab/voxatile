package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel with an additional {@code int} index.
 */
public interface IndexedVoxel3d extends Voxel3d {
    /**
     * The index of this voxel.
     *
     * @return the voxel index.
     */
    int index();

    /**
     * Default implementation with coordinate and index.
     *
     * @param coords the voxel coordinate.
     * @param index the voxel index.
     */
    record Impl(WorldCoords3d coords, int index) implements IndexedVoxel3d {}
}
