package com.ignfab.minalac.generator.parameters.processors.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.ignfab.minalac.generator.processors.post.MetadataParsePostProcessor.ParsingFailurePolicy;

/**
 * Parameters for {@link ParsingFailurePolicy}.
 *
 * Avoids scattering configuration-specific names
 * for {@link ParsingFailurePolicy} across the codebase.
 */
public enum FailurePolicyParams {
    /**
     * Maps the "ignore" JSON value to {@link ParsingFailurePolicy}.
     */
    @JsonProperty("ignore")
    IGNORE(ParsingFailurePolicy.IGNORE),

    /**
     * Maps the "removeMetadata" JSON value to {@link ParsingFailurePolicy}.
     */
    @JsonProperty("removeMetadata")
    REMOVE_METADATA(ParsingFailurePolicy.REMOVE_METADATA),

    /**
     * Maps the "discardModel" JSON value to {@link ParsingFailurePolicy}.
     */
    @JsonProperty("discardModel")
    DISCARD_MODEL(ParsingFailurePolicy.DISCARD_MODEL),

    /**
     * Maps the "error" JSON value to {@link ParsingFailurePolicy}.
     */
    @JsonProperty("error")
    ERROR(ParsingFailurePolicy.ERROR);

    private final ParsingFailurePolicy parsingFailurePolicy;

    FailurePolicyParams(ParsingFailurePolicy parsingFailurePolicy) {
        this.parsingFailurePolicy = parsingFailurePolicy;
    }

    /**
     * Returns the associated {@link ParsingFailurePolicy}.
     *
     * @return the corresponding {@link ParsingFailurePolicy}
     */
    public ParsingFailurePolicy create() {
        return parsingFailurePolicy;
    }
}
