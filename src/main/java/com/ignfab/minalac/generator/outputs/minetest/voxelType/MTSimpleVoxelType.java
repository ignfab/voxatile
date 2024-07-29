package com.ignfab.minalac.generator.outputs.minetest.voxelType;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelType;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;

/**
 * A concrete implementation of {@link MTVoxelType}.
 * Used when there is no need to have specific values for {@code param1} and {@code param2}.
 */
public class MTSimpleVoxelType extends MTVoxelType {
    /**
     * Constructs a new {@code MTSimpleVoxelType}.
     *
     * @param world the {@link MTVoxelWorld} in which the voxel can be placed
     * @param type the node type string
     */
    public MTSimpleVoxelType(MTVoxelWorld world, String type) {
        super(world, type, (byte) 0, (byte) 0);
    }
}
