package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.OutOfWorldException;
import com.ignfab.minalac.generator.world.VoxelType;

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
        //The y-axis in Minetest corresponds, in our chosen coordinate system, to the z-axis, hence the inversion
        this.world.set(x, z, y, this);
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