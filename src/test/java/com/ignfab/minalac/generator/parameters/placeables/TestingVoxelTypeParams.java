package com.ignfab.minalac.generator.parameters.placeables;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelType;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A voxel type parameters for simple Minetest voxel types with only node type name.
 */
@SuppressWarnings("VisibilityModifier")
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public class TestingVoxelTypeParams extends CustomPlaceableParams {
    /**
     * Node type name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String type;

    /**
     * Creates a new {@code TestingVoxelTypeParams}.
     *
     * @param type Voxel type name
     */
    @ConstructorProperties({"type"})
    public TestingVoxelTypeParams(String type) {
        if (type == "")
            throw new IllegalArgumentException("type should not be empty");
        this.type = type;
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (world instanceof TestingVoxelWorld)
            return new TestingVoxelType((TestingVoxelWorld) world, type);
        return null;
    }
}
