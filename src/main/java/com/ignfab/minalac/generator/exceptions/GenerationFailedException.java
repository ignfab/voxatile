package com.ignfab.minalac.generator.exceptions;

/**
 * Exception thrown when a fatal error occurs. This means that nothing can be
 * done to recover from this situation, and the program should stop working.
 */
public class GenerationFailedException extends Exception {
    /**
     * Creates a new generation failure.
     * @param message the error message
     */
    public GenerationFailedException(String message) {
        super(message);
    }

    /**
     * Creates a new generation failure.
     * @param cause the cause of this error
     */
    public GenerationFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new generation failure.
     * @param message the error message
     * @param cause the cause of this error
     */
    public GenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
