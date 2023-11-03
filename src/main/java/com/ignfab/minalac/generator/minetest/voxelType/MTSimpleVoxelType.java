package com.ignfab.minalac.generator.minetest.voxelType;

import com.ignfab.minalac.generator.minetest.MTVoxelWorld;

public class MTSimpleVoxelType extends MTVoxelType {
    public MTSimpleVoxelType(MTVoxelWorld world, String type) {
        super(world, type, (byte) 0, (byte) 0);
    }
}