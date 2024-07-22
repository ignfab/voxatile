package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel with an additional {@code int} index.
 */
public interface IndexedVoxel2d extends Voxel2d {
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
    record Impl(WorldCoords2d coords, int index) implements IndexedVoxel2d {}
}
