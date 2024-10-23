package com.ignfab.minalac.generator.parameters.placeables.minecraft;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelType;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A voxel type parameters for simple Minecraft voxel types with only block type name.
 */
@SuppressWarnings("VisibilityModifier")
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public class MCVoxelTypeParams extends PlaceableParams {
    /**
     * Block type name (required).
     */
    public String block;

    /**
     * Creates a new {@code MCSimpleVoxelTypeParams}.
     *
     * @param block Block type name
     */
    @ConstructorProperties({"block"})
    public MCVoxelTypeParams(String block) {
        if (block == "")
            throw new IllegalArgumentException("block should not be empty");
        this.block = block;
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (world instanceof MCVoxelWorld)
            return new MCVoxelType((MCVoxelWorld) world, block);
        throw new IllegalArgumentException("Voxel type does not match voxel world format");
    }
}
