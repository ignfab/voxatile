package com.ignfab.minalac.generator.outputs.minetest.voxelType;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelType;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;

public class MTSimpleVoxelType extends MTVoxelType {
    public MTSimpleVoxelType(MTVoxelWorld world, String type) {
        super(world, type, (byte) 0, (byte) 0);
    }
}