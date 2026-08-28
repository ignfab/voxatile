package com.ignfab.minalac.generator.exceptions;

/**
 * An exception thrown when a builder cannot build.
 * <p>
 * This may happen when building or as soon as the impossibility is detected.
 */
public class UnbuildableException extends Exception {

    /**
     * Creates a new unbuildable exception.
     * @param message the error message
     */
    public UnbuildableException(String message) {
        super(message);
    }

    /**
     * Creates a new unbuildable exception.
     * @param cause the cause of this error
     */
    public UnbuildableException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new unbuildable exception.
     * @param message the error message
     * @param cause the cause of this error
     */
    public UnbuildableException(String message, Throwable cause) {
        super(message, cause);
    }
}

