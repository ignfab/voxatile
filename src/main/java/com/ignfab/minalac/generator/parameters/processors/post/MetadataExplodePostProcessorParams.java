package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.processors.post.MetadataExplodePostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataExplodePostProcessor}.
 */
public class MetadataExplodePostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata to explode (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String metadata;

    /**
     * Optional prefix to prepend to exploded metadata names (optional, default none).
     */
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public String prefix;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param metadata name of the metadata to explode
     */
    @ConstructorProperties("metadata")
    public MetadataExplodePostProcessorParams(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public PostProcessor<Model, ?> create() {
        return new MetadataExplodePostProcessor(metadata, prefix);
    }
}
