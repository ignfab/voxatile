package com.ignfab.minalac.generator.parameters.placeables.voxels;

import com.ignfab.minalac.generator.world.NoVoxel;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Places nothing.
 */
public class NoVoxelParams extends VoxelParams {
    @Override
    public Placeable create(VoxelWorld world) {
        return NoVoxel.INSTANCE;
    }
}
