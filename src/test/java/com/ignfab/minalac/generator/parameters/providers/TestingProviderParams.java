package com.ignfab.minalac.generator.parameters.providers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.inputs.Provider;
import com.ignfab.minalac.generator.inputs.TestingProvider;
import com.ignfab.minalac.generator.parameters.processors.ProcessorParams;
import com.ignfab.minalac.generator.parameters.processors.TestingProcessorParams;

public class TestingProviderParams extends ProviderParams {
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
    public Provider<?> create(Generation generation) {
        return new TestingProvider(generation.crs());
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new TestingProcessorParams();
    }
}
