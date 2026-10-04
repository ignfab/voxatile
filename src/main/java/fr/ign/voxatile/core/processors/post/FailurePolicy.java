package fr.ign.voxatile.core.processors.post;

import fr.ign.voxatile.core.exceptions.GenerationFailedException;
import fr.ign.voxatile.core.exceptions.IgnorableException;

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
