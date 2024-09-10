package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel is a {@link WorldCoords2d} that can have additional information.
 *
 * @see LineVoxel2d
 */
public interface Voxel2d {
    /**
     * The coordinate of this voxel.
     *
     * @return the voxel coordinate.
     */
    WorldCoords2d coords();
}
