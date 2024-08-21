package com.ignfab.minalac.generator.outputs.minetest;

import java.util.HashMap;

/**
 * This class is a representation of what Minetest refers as block.
 * In Minetest, a world is composed of blocks, where a block consists of 16x16x16 voxels (or nodes per Minetest terminology).
 *
 * @see <a href="https://github.com/minetest/minetest/blob/master/doc/world_format.md#map-file-format">Minetest world format</a>
 */
public class Block {
    private short[] param0;
    private byte[] param1;
    private byte[] param2;
    private HashMap<Integer, String> nameIdMapping;
    private HashMap<String, Integer> idNameMapping;

    /**
     * Constructs a new {@code Block}.
     */
    public Block() {
        // Array length defined by map version (https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-data)
        this.param0 = new short[4096];
        this.param1 = new byte[4096];
        this.param2 = new byte[4096];
        this.nameIdMapping = new HashMap<>();
        this.idNameMapping = new HashMap<>();
        this.nameIdMapping.put(0, "air");
        this.idNameMapping.put("air", 0);
    }

    /**
     * Places the specified voxel on the block by updating the block's three array parameters.
     * The coordinate system is the one used in Minetest.
     *
     * @param x the x-coordinate value relatively to the block
     * @param y the y-coordinate value relatively to the block
     * @param z the z-coordinate value relatively to the block
     * @param voxel the {@link MTVoxelType} to place within the block
     */
    public void set(int x, int y, int z, MTVoxelType voxel) {
        // Node location on arrays
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-data
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

    /**
     * Returns a mapping of IDs to node names for this block.
     *
     * @return a {@code HashMap<Integer, String>} where the values are node names
     */
    public HashMap<Integer, String> getNameIdMapping() {
        return nameIdMapping;
    }

    /**
     * Returns the param0 array parameter of this block.
     *
     * @return the param0 {@code short} array parameter
     */
    public short[] getParam0() {
        return param0;
    }

    /**
     * Returns the param1 array parameter of this block.
     *
     * @return the param1 {@code byte} array parameter
     */
    public byte[] getParam1() {
        return param1;
    }

    /**
     * Returns the param2 array parameter of this block.
     *
     * @return the param2 {@code byte} array parameter
     */
    public byte[] getParam2() {
        return param2;
    }
}
