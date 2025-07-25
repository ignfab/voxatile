package com.ignfab.minalac.generator.parameters.models;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;

/**
 * A {@code ModelSelectionParams} for testing purposes.
 */
public class TestingModelSelectionParams extends ModelSelectionParams {
    /**
     * An invalid testing model selection.
     */
    public static final TestingModelSelectionParams INVALID = new TestingModelSelectionParams("");
    /**
     * A valid testing model selection.
     */
    public static final TestingModelSelectionParams VALID = new TestingModelSelectionParams("valid");

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
    @ConstructorProperties("requiredField")
    public TestingModelSelectionParams(String requiredField) {
        super(requiredField);
        this.requiredField = requiredField;
    }

    @Override
    public void validate() {
        if (requiredField.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public ModelSelection create(ModelStore store) {
        return null;
    }
}
