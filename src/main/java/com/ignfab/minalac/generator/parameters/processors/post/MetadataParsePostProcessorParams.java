package com.ignfab.minalac.generator.parameters.processors.post;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.ValueParser;
import com.ignfab.minalac.generator.processors.post.MetadataFunctionPostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

/**
 * Parameters for parsing post processor.
 */
public class MetadataParsePostProcessorParams extends PostProcessorParams {
    /**
     * Name of metadata to parse (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final String metadata;

    /**
     * Type of parsed value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final ValueParser<?> as;

    /**
     * Policy to apply when the metadata is absent (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public FailurePolicyParams ifMissing = FailurePolicyParams.ERROR;

    /**
     * Policy to apply when the {@code parser} returns null (optional).
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
    public MetadataParsePostProcessorParams(String metadata, ValueParser<?> as) {
        this.metadata = metadata;
        this.as = as;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
    }

    @Override
    public PostProcessor<Model, Model> create() {
        @SuppressWarnings("unchecked")
        ValueParser<Object> parser = (ValueParser<Object>) as;

        return new MetadataFunctionPostProcessor<>(
            parser.type(),
            metadata,
            parser.parser(),
            ifMissing.create(),
            ifNotParsable.create()
        );
    }
}
