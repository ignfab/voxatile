package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;
import com.ignfab.minalac.generator.processors.post.MetadataDefaultPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataDefaultPostProcessor}.
 */
public class MetadataDefaultPostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final StringNotBlank metadata;

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
    public MetadataDefaultPostProcessorParams(StringNotBlank metadata, String value, ValueParser<?> as) {
        this.metadata = metadata;
        this.value = value;
        this.as = as;
    }

    @Override
    public PostProcessor<Model, Model> create() {
        return new MetadataDefaultPostProcessor(metadata.create(), as.parse(value));
    }
}
