package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelType;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A voxel type parameters for simple Minecraft voxel types with only block type name.
 */
public class MCVoxelTypeParams extends VoxelParams {
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
     * Creates a new {@code MCVoxelTypeParams}.
     *
     * @param block Block type name
     */
    @ConstructorProperties({"block"})
    public MCVoxelTypeParams(String block) {
        this.block = block;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (block.isBlank())
            throw new IllegalArgumentException("block should not be empty or blank");
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (world instanceof MCVoxelWorld mcWorld)
            return new MCVoxelType(mcWorld, block, properties);
        throw new IllegalArgumentException("Voxel type does not match voxel world format");
    }
}
