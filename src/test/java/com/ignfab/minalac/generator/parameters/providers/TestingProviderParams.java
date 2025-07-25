package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.TestingProvider;

/**
 * A ProviderParams class for testing purposes.
 */
public class TestingProviderParams extends ProviderParams {
    /**
     * An invalid testing provider.
     */
    public static final TestingProviderParams INVALID = new TestingProviderParams("");
    /**
     * A valid testing provider.
     */
    public static final TestingProviderParams VALID = new TestingProviderParams("valid");
    /**
     * A required field.
     */
    public String requiredField;
    /**
     * An optional field.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public String optionalField = "defaultOptionalValue";

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param requiredField the required field.
     */
    @ConstructorProperties({"requiredField"})
    public TestingProviderParams(String requiredField) {
        this.requiredField = requiredField;
    }

    @Override
    public void validate() {
        if (requiredField.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public Provider<?> create(Generation generation) {
        return new TestingProvider(generation.crs());
    }

}
