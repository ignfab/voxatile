package com.ignfab.minalac.generator.parameters.placeables.voxels;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.world.NoVoxel;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Places nothing.
 */
@JsonDeserialize // Avoids infinite loop, jackson reusing deserializer when deserializer tries to deserialize CustomPlaceableParams
public class NoVoxelParams extends VoxelParams {
    @Override
    public Placeable create(VoxelWorld world) {
        return NoVoxel.INSTANCE;
    }
}
