package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.parameters.utils.StringNotBlank;
import com.ignfab.minalac.generator.processors.post.MetadataParsePostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for {@link MetadataParsePostProcessor}.
 */
public class MetadataParsePostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata to parse (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final StringNotBlank metadata;

    /**
     * Type of parsed value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final ValueParser<?> as;

    /**
     * Parsing function to use when the metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifMissing = FailurePolicyParams.ERROR;

    /**
     * Parsing function to use when the {@code parser} return null (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifNotParsable = FailurePolicyParams.ERROR;

    /**
     * Constructor used to ensure that the required fields
     * are present during deserialization.
     *
     * @param metadata name of the metadata to parse
     * @param as type of parsed value
     */
    @ConstructorProperties({ "metadata", "as" })
    public MetadataParsePostProcessorParams(StringNotBlank metadata, ValueParser<?> as) {
        this.metadata = metadata;
        this.as = as;
    }

    @Override
    public PostProcessor<Model, Model> create() {
        @SuppressWarnings("unchecked")
        ValueParser<Object> parser = (ValueParser<Object>) as;

        return new MetadataParsePostProcessor<>(
            parser.type(),
            metadata.create(),
            parser.parser(),
            ifMissing.create(),
            ifNotParsable.create()
        );
    }
}
