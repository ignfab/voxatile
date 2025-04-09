package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxel;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for Minetest voxels with only node name.
 */
public class MTVoxelParams extends PlaceableParams {
    /**
     * Node name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String node;

    /**
     * Node param1 (Refer to Minetest documentation).
     */
    public byte param1 = 0;

    /**
     * Node param2 (Refer to Minetest documentation).
     */
    public byte param2 = 0;

    /**
     * Creates a new {@code MTVoxelParams}.
     *
     * @param node Node type name
     */
    @ConstructorProperties({"node"})
    public MTVoxelParams(String node) {
        this.node = node;
    }

    @Override
    public void validate() {
        if (node.isBlank())
            throw new IllegalArgumentException("node should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new MTVoxel(node, param1, param2);
    }
}
