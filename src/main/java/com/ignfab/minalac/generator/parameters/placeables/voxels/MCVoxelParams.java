package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minecraft.MCVoxel;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Parameters for simple Minecraft voxel with only block type name.
 */
public class MCVoxelParams extends PlaceableParams {
    /**
     * Block type name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank block;

    /**
     * Block properties (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public Map<String, String> properties = null;

    /**
     * Creates a new {@code MCVoxelParams}.
     *
     * @param block Block type name
     */
    @ConstructorProperties({"block"})
    public MCVoxelParams(StringNotBlank block) {
        this.block = block;
    }

    @Override
    public Placeable create(Seed seed) {
        return new MCVoxel(block.create(), properties);
    }

    public static MCVoxelParams fromString(String block) {
        return new MCVoxelParams(new StringNotBlank(block));
    }
}
