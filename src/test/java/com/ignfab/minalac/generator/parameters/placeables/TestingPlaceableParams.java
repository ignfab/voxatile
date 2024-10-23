package com.ignfab.minalac.generator.parameters.placeables;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ignfab.minalac.generator.world.Placeable;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * A placeable params for testing.
 */
@SuppressWarnings("VisibilityModifier")
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type
public class TestingPlaceableParams extends CustomPlaceableParams {
    /**
     * Name field (required).
     */
    public String name;

    /**
     * Param field - actually ignored in TestingVoxelWorld (optional).
     */
    public String param;

    /**
     * A method to create a voxel world (actually does nothing, just for testing purpose).
     *
     * @return a null {@code VoxelWorld}
     */
    public static VoxelWorld worldCreator() {
        return null;
    }

    @Override
    public Placeable create(VoxelWorld world) {
        return null;
    }

    /**
     * Creates a new TestingPlaceableParams.
     *
     * @param name Name field value
     */
    @ConstructorProperties({"name"})
    public TestingPlaceableParams(String name) {
        this.name = name;
    }
}
