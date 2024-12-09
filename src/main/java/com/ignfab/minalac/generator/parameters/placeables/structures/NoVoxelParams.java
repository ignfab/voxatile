package com.ignfab.minalac.generator.parameters.placeables.structures;

import com.ignfab.minalac.generator.parameters.placeables.CustomPlaceableParams;
import com.ignfab.minalac.generator.world.NoVoxel;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Places nothing.
 */
public class NoVoxelParams extends CustomPlaceableParams {
    @Override
    public Placeable create(VoxelWorld world) {
        return NoVoxel.INSTANCE;
    }
}
