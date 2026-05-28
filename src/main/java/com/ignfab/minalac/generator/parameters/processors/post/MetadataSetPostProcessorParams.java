package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
import com.ignfab.minalac.generator.processors.post.MetadataSetPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataSetPostProcessor}.
 */
public class MetadataSetPostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata to define (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Model value to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams value;

    /**
     * Whether to abort if metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public boolean abortIfValueIsAbsent = false;

    /**
     * Whether to keep existing metadata or not (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public boolean keepExisting = false;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param metadata name of the metadata to define
     * @param value model value to use
     */
    @ConstructorProperties({ "metadata", "value" })
    public MetadataSetPostProcessorParams(String metadata, ModelValueParams value) {
        this.metadata = metadata;
        this.value = value;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
        value.validate();
    }

    @Override
    public PostProcessor<Model, ?> create(Generation generation) {
        return new MetadataSetPostProcessor(
            metadata,
            value.create(generation),
            abortIfValueIsAbsent,
            keepExisting
        );
    }
}
