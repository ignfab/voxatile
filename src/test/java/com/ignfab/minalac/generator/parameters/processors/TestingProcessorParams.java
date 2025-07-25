package com.ignfab.minalac.generator.parameters.processors;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.TestingProcessor;

/**
 * A testing ProcessorParams.
 */
public class TestingProcessorParams extends ProcessorParams {
    /**
     * An invalid testing processor.
     */
    public static final TestingProcessorParams INVALID = new TestingProcessorParams("");
    /**
     * A valid testing processor.
     */
    public static final TestingProcessorParams VALID = new TestingProcessorParams("valid");
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
    public TestingProcessorParams(String requiredField) {
        this.requiredField = requiredField;
    }

    @Override
    public void validate() {
        if (requiredField.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public Processor<?, ?> create(Generation generation) {
        return new TestingProcessor();
    }
}
