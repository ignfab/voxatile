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
     * @param nodeX the x-coordinate value relatively to the block
     * @param nodeY the y-coordinate value relatively to the block
     * @param nodeZ the z-coordinate value relatively to the block
     * @param voxel the {@link MTVoxel} to place within the block
     */
    public void set(int nodeX, int nodeY, int nodeZ, MTVoxel voxel) {
        int i = nodeCoordsToIndex(nodeX, nodeY, nodeZ);
        param0[i] = getOrCreateIdForType(voxel.getType());
        param1[i] = voxel.getParam1();
        param2[i] = voxel.getParam2();
    }

    /**
     * Gets or creates a new id for given type name.
     * This ensure a threadsafe id creation.
     *
     * @param name type name
     * @return identfier for the given type name
     */
    private short getOrCreateIdForType(String name) {
        // Search first in case we already have that name (most likely)
        if (idNameMapping.containsKey(name))
            return idNameMapping.get(name).shortValue();

        synchronized (idNameMapping) {
            // Check again to be sure name has not been created since previous check
            if (idNameMapping.containsKey(name))
                return idNameMapping.get(name).shortValue();

            // Create a new Id for that name
            int newId = nameIdMapping.size();
            nameIdMapping.put(newId, name);
            idNameMapping.put(name, newId);
            return (short) newId;
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

    private int nodeCoordsToIndex(int nodeX, int nodeY, int nodeZ) {
        // Node location on arrays
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-data
        return nodeZ << 8 | nodeY << 4 | nodeX;
    }

    protected MTVoxel get(int nodeX, int nodeY, int nodeZ) {
        int i = nodeCoordsToIndex(nodeX, nodeY, nodeZ);
        return new MTVoxel(nameIdMapping.get((int) param0[i]), param1[i], param2[i]);
    }
}
