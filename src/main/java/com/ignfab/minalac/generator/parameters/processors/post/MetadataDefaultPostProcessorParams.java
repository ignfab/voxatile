package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.processors.post.PostProcessor;
import com.ignfab.minalac.generator.processors.post.MetadataDefaultPostProcessor;

/**
 * Parameters for {@link MetadataDefaultPostProcessor}.
 */
public class MetadataDefaultPostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final String metadata;

    /**
     * Default value to use if the metadata is not present (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final String value;

    /**
     * Type to which the value should be converted (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final ValueParser<?> as;

    /**
     * Constructor used to ensure that the required fields are present during
     * deserialization.
     *
     * @param metadata name of the metadata
     * @param value default value to use if the metadata is not present
     * @param as type to which the value should be converted
     */
    @ConstructorProperties({ "metadata", "value", "as" })
    public MetadataDefaultPostProcessorParams(String metadata, String value, ValueParser<?> as) {
        this.metadata = metadata;
        this.value = value;
        this.as = as;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public PostProcessor<Model, Model> create() {
        return new MetadataDefaultPostProcessor(metadata, as.parse(value));
    }
}
