package com.ignfab.minalac.generator.minetest;

import com.ignfab.minalac.generator.minetest.voxelType.MTVoxelType;

import java.util.HashMap;

public class Block {
    private short[] param0;
    private byte[] param1;
    private byte[] param2;
    private HashMap<Integer, String> nameIdMapping;
    private HashMap<String, Integer> idNameMapping;

    public Block() {
        //Array length defined by map version (https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-data)
        this.param0 = new short[4096];
        this.param1 = new byte[4096];
        this.param2 = new byte[4096];
        this.nameIdMapping = new HashMap<>();
        this.idNameMapping = new HashMap<>();
        this.nameIdMapping.put(0, "air");
        this.idNameMapping.put("air", 0);
    }

    public void set(int x, int y, int z, MTVoxelType voxel) {
        //Node location on arrays
        //https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-data
        int i = z << 8 | y << 4 | x;
        param1[i] = voxel.getParam1();
        param2[i] = voxel.getParam2();

        if (idNameMapping.containsKey(voxel.getType())) {
            param0[i] = idNameMapping.get(voxel.getType()).shortValue();
        } else {
            int newId = nameIdMapping.size();
            nameIdMapping.put(newId, voxel.getType());
            idNameMapping.put(voxel.getType(), newId);
            param0[i] = (short) newId;
        }
    }

    public HashMap<Integer, String> getNameIdMapping() {
        return nameIdMapping;
    }

    public short[] getParam0() {
        return param0;
    }

    public byte[] getParam1() {
        return param1;
    }

    public byte[] getParam2() {
        return param2;
    }
}
