package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

/**
 * A {@code CustomHeightmapParams} and a {@code StoredHeightmapParams} for testing purposes.
 */
public class TestingHeightmapParams extends WritableHeightmapParams {
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
        super(requiredField);
        this.requiredField = requiredField;
    }

    @Override
    public void validate() {
        if (requiredField.isBlank())
            throw new IllegalArgumentException();
    }
}
