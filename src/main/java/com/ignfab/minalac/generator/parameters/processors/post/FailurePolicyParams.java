package com.ignfab.minalac.generator.parameters.processors.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.ignfab.minalac.generator.processors.post.FailurePolicy;

/**
 * Parameters for {@link FailurePolicy}.
 *
 * Avoids scattering configuration-specific names
 * for {@link FailurePolicy} across the codebase.
 */
public enum FailurePolicyParams {
    /**
     * Maps the "ignore" JSON value to {@link FailurePolicy}.
     */
    @JsonProperty("ignore")
    IGNORE(FailurePolicy.IGNORE),

    /**
     * Maps the "removeMetadata" JSON value to {@link FailurePolicy}.
     */
    @JsonProperty("removeMetadata")
    REMOVE_METADATA(FailurePolicy.REMOVE_METADATA),

    /**
     * Maps the "discardModel" JSON value to {@link FailurePolicy}.
     */
    @JsonProperty("discardModel")
    DISCARD_MODEL(FailurePolicy.DISCARD_MODEL),

    /**
     * Maps the "error" JSON value to {@link FailurePolicy}.
     */
    @JsonProperty("error")
    ERROR(FailurePolicy.ERROR);

    private final FailurePolicy failurePolicy;

    FailurePolicyParams(FailurePolicy failurePolicy) {
        this.failurePolicy = failurePolicy;
    }

    /**
     * Returns the associated {@link FailurePolicy}.
     *
     * @return the corresponding {@link FailurePolicy}
     */
    public FailurePolicy create() {
        return failurePolicy;
    }
}
