package com.ignfab.minalac.generator.parameters.placeables;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Class for testing placeable params.
 */
public class TestingPlaceableParams extends PlaceableParams {
    /**
     * An invalid testing placeable params.
     */
    public static final TestingPlaceableParams INVALID = new TestingPlaceableParams("");
    /**
     * A valid testing placeable params.
     */
    public static final TestingPlaceableParams VALID = new TestingPlaceableParams("voxel");
    /**
     * Voxel name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String name;
    private Placeable placeable;

    /**
     * Creates a new {@code TestingVoxelParams}.
     *
     * @param name Voxel name
     */
    @ConstructorProperties({"name"})
    public TestingPlaceableParams(String name) {
        this.name = name;
        // TODO-PR: Temp, redondant only for TestingPlaceable in TestingPlaceableParams
        this.placeable = new TestingVoxel(name);
    }

    public TestingPlaceableParams(String name, Placeable placeable) {
        this.name = name;
        this.placeable = placeable;
    }

    @Override
    public void validate() {
        if (name.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public Placeable create(Seed seed) {
        return placeable;
    }
}
