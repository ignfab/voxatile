package com.ignfab.minalac.generator.parameters.placeables.minetest;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelType;
import com.ignfab.minalac.generator.parameters.placeables.CustomPlaceableParams;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A voxel type parameters for Minetest voxel types with only node type name.
 */
@SuppressWarnings("VisibilityModifier")
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public class MTVoxelTypeParams extends CustomPlaceableParams {
    /**
     * Node type name (required).
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
     * Creates a new {@code MTVoxelTypeParams}.
     *
     * @param node Node type name
     */
    @ConstructorProperties({"node"})
    public MTVoxelTypeParams(String node) {
        this.node = node;
    }

    @Override
    public void validate() {
        if (node.isBlank())
            throw new IllegalArgumentException("node should not be empty or blank");
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (world instanceof MTVoxelWorld mtWorld)
            return new MTVoxelType(mtWorld, node, param1, param2);
        throw new IllegalArgumentException("Voxel type does not match voxel world format");
    }
}
