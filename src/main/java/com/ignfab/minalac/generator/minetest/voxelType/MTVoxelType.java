package com.ignfab.minalac.generator.minetest.voxelType;

import com.ignfab.minalac.generator.OutOfWorldException;
import com.ignfab.minalac.generator.VoxelType;
import com.ignfab.minalac.generator.minetest.MTVoxelWorld;

public abstract class MTVoxelType implements VoxelType {
    protected MTVoxelWorld world;
    protected String type;
    protected byte param1;
    protected byte param2;

    public MTVoxelType(MTVoxelWorld world, String type, byte param1, byte param2) {
        this.world = world;
        this.type = type;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void place(int x, int y, int z) throws OutOfWorldException {
        this.world.set(x, y, z, this);
    }

    public String getType() {
        return type;
    }

    public byte getParam1() {
        return param1;
    }

    public byte getParam2() {
        return param2;
    }
}