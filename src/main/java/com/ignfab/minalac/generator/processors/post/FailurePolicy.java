package com.ignfab.minalac.generator.processors.post;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;

/**
 * Policies about how to handle failure.
 */
public enum FailurePolicy {
    /**
     * Ignores the failure, leaving everything untouched.
     */
    IGNORE,
    /**
     * Removes the metadata that caused the failure.
     */
    REMOVE_METADATA,
    /**
     * Throws an {@link IgnorableException} to discard the model.
     */
    DISCARD_MODEL,
    /**
     * Throws an {@link GenerationFailedException} causing a fatal error.
     */
    ERROR
}
