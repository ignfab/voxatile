package com.ignfab.minalac.generator.parameters.placeables.voxels;

import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Places nothing.
 */
public class NoVoxelParams extends VoxelParams {
    @Override
    public Placeable create(Seed seed) {
        return NoVoxel.INSTANCE;
    }
}
