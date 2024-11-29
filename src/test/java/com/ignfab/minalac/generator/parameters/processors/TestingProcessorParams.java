package com.ignfab.minalac.generator.parameters.processors;

import java.beans.ConstructorProperties;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.processors.Processor;
import com.ignfab.minalac.generator.processors.TestingProcessor;

public class TestingProcessorParams extends ProcessorParams {
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
    public Processor<?, ?> create(Generation generation, CoordinateReferenceSystem layerCrs) {
        return new TestingProcessor();
    }
}
