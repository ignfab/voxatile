package com.ignfab.minalac.generator.parameters.processors.post;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.parameters.processors.post.parsers.ValueParsers;
import com.ignfab.minalac.generator.processors.post.MetadataParsePostProcessor;
import com.ignfab.minalac.generator.processors.post.PostProcessor;

import java.beans.ConstructorProperties;

/**
 * Parameters for {@link MetadataParsePostProcessor}.
 */
public class MetadataParsePostProcessorParams extends PostProcessorParams {
    /**
     * Name of the metadata to parse (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final String metadata;

    /**
     * Type of parsed value (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public final String as;

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
    public MetadataParsePostProcessorParams(String metadata, String as) {
        this.metadata = metadata;
        this.as = as;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (metadata.isBlank())
            throw new IllegalArgumentException("The 'metadata' field cannot be empty or contain only whitespace.");
        ValueParsers.validate(as);
    }

    @Override
    public PostProcessor<Model, Model> create() {
        ValueParsers.Parser<Object> parser = ValueParsers.get(as);
        return new MetadataParsePostProcessor<>(
            metadata,
            parser.type(),
            parser.parser(),
            ifMissing.create(),
            ifNotParsable.create()
        );
    }
}
