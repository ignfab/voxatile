package com.ignfab.minalac.generator.parameters.placeables.voxels;

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
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public class TestingVoxelTypeParams extends VoxelParams {
    /**
     * Node type name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String name;

    /**
     * Creates a new {@code TestingVoxelTypeParams}.
     *
     * @param name Voxel type name
     */
    @ConstructorProperties({"name"})
    public TestingVoxelTypeParams(String name) {
        this.name = name;
    }

    @Override
    public Placeable create(VoxelWorld world) {
        if (world instanceof TestingVoxelWorld testingWorld)
            return new TestingVoxelType(testingWorld, name);
        return null;
    }
}
