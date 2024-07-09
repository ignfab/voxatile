package com.ignfab.minalac.generator.exceptions;

/**
 * Exception thrown when something goes wrong related to coordinates transformation.
 */
public class TransformException extends Exception {
        /**
     * Creates a new transform exception.
     * @param message the error message
     */
    public TransformException(String message) {
        super(message);
    }

    /**
     * Creates a new transform exception.
     * @param cause the cause of this error
     */
    public TransformException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new transform exception.
     * @param message the error message
     * @param cause the cause of this error
     */
    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }
}
