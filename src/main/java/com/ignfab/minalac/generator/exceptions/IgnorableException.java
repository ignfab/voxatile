package com.ignfab.minalac.generator.exceptions;

/**
 * Exception thrown when something goes wrong with an element that can be ignored.
 * The code receiving this error can decide to continue without the problematic
 * element, or completely abort and cause a fatal error.
 */
public class IgnorableException extends Exception {
    /**
     * Creates a new ignorable exception.
     * @param message the error message
     */
    public IgnorableException(String message) {
        super(message);
    }

    /**
     * Creates a new ignorable exception.
     * @param cause the cause of this error
     */
    public IgnorableException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new ignorable exception.
     * @param message the error message
     * @param cause the cause of this error
     */
    public IgnorableException(String message, Throwable cause) {
        super(message, cause);
    }
}
