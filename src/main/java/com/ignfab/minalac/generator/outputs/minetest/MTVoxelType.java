package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.world.VoxelType;

/**
 * {@code MTVoxelType} is an abstract class to provide the necessary structure and mechanism in order to implement {@link VoxelType} for Minetest.
 * A voxel in Minetest, known as node, consists of three parameters: type, param1, param2.
 * @see <a href="https://github.com/minetest/minetest/blob/master/src/mapnode.h#L138">Minetest's MapNode class</a> for more information about the node's parameters
 */
public class MTVoxelType implements VoxelType {
    /**
     * The Minetest World object.
     */
    protected MTVoxelWorld world;
    /**
     * The node type string.
     * @see <a href="https://wiki.minetest.net/Games/Minetest_Game/Nodes">List of node types (Minetest Wiki)</a>
     */
    protected String type;
    /**
     * The param1 data of the node.
     * This parameter usually contains information about the node's light intensity.
     */
    protected byte param1;
    /**
     * The param2 data of the node.
     * This parameter usually contains information about the node's spacial orientation.
     */
    protected byte param2;

    /**
     * Constructs a new {@code MTVoxelType}.
     *
     * @param world the {@link MTVoxelWorld} in which the voxel can be placed
     * @param type the node type string
     * @param param1 the param1 data of the node
     * @param param2 the param2 data of the node
     * @see <a href="https://github.com/minetest/minetest/blob/master/src/mapnode.h#L138">Minetest's MapNode class</a> for more information about the node's parameters
     */
    public MTVoxelType(MTVoxelWorld world, String type, byte param1, byte param2) {
        this.world = world;
        this.type = type;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public void place(int x, int y, int z)  {
        // The y-axis in Minetest corresponds, in our chosen coordinate system, to the z-axis, hence the inversion
        this.world.set(x, z, y, this);
    }

    /**
     * Returns the node type string.
     * @return the node type string
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the param1 data of the node.
     * This parameter usually contains information about the node's light intensity.
     * @return the param1 data of the node.
     */
    public byte getParam1() {
        return param1;
    }

    /**
     * Returns the param2 data of the node.
     * This parameter usually contains information about the node's spacial orientation.
     * @return the param2 data of the node
     */
    public byte getParam2() {
        return param2;
    }
}
