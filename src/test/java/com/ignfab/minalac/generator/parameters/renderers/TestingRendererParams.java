package com.ignfab.minalac.generator.parameters.renderers;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.renderers.Renderer;

/**
 * A RendererParams class for testing purposes.
 */
public class TestingRendererParams extends RendererParams {
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
    public TestingRendererParams(String requiredField) {
        this.requiredField = requiredField;
    }

    @Override
    public Renderer create(Generation generation) {
        return null;
    }
}
