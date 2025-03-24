package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;

/**
 * A {@code CustomHeightmapParams} and a {@code StoredHeightmapParams} for testing purposes.
 */
public class TestingHeightmapParams extends StoredHeightmapParams {
    /**
     * An invalid testing heightmap.
     */
    public static final TestingHeightmapParams INVALID = new TestingHeightmapParams("");
    /**
     * A valid testing heightmap.
     */
    public static final TestingHeightmapParams VALID = new TestingHeightmapParams("thisIsEmpty");
    /**
     * A required field.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String requiredField;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param requiredField the required field.
     */
    @ConstructorProperties({"requiredField"})
    public TestingHeightmapParams(String requiredField) {
        super("stored");
        this.requiredField = requiredField;
    }

    @Override
    public void validate() {
        if (requiredField.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public Heightmap create(Generation generation) {
        return null;
    }
}
