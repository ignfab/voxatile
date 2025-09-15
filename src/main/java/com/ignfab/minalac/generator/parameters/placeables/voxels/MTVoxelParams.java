package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxel;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;
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
    public StringNotBlank node;

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
    public MTVoxelParams(StringNotBlank node) {
        this.node = node;
    }

    @Override
    public Placeable create(Seed seed) {
        return new MTVoxel(node.create(), param1, param2);
    }

    public static MTVoxelParams fromString(String node) {
        return new MTVoxelParams(new StringNotBlank(node));
    }
}
