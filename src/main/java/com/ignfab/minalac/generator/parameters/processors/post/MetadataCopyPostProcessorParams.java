package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;
import com.ignfab.minalac.generator.processors.post.MetadataCopyPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataCopyPostProcessor}.
 */
public class MetadataCopyPostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata to copy (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank metadata;

    /**
     * Name of copied metadata (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public StringNotBlank to;

    /**
     * Whether to abort if metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public boolean abortIfMetadataIsAbsent = false;

    /**
     * Whether to keep existing metadata or not (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public boolean keepExisting = false;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param metadata name of the metadata to copy
     * @param to name of copied metadata
     */
    @ConstructorProperties({"metadata", "to"})
    public MetadataCopyPostProcessorParams(StringNotBlank metadata, StringNotBlank to) {
        this.metadata = metadata;
        this.to = to;
    }

    @Override
    public PostProcessor<Model, ?> create() {
        return new MetadataCopyPostProcessor(
            metadata.create(),
            to.create(),
            abortIfMetadataIsAbsent,
            keepExisting
        );
    }
}
