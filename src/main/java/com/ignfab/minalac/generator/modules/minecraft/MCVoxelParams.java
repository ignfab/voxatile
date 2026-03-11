package com.ignfab.minalac.generator.modules.minecraft;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
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
    public String block;

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
    public MCVoxelParams(String block) {
        this.block = block;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (block.isBlank())
            throw new IllegalArgumentException("block should not be empty or blank");
    }

    @Override
    public Placeable create(Seed seed) {
        return new MCVoxel(block, properties);
    }
}
