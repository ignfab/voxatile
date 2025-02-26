package com.ignfab.minalac.generator.parameters.placeables.voxels;

import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Places nothing.
 */
public class NoVoxelParams extends VoxelParams {
    @Override
    public Placeable create(Seed seed, VoxelWorld world) {
        return NoVoxel.INSTANCE;
    }
}
