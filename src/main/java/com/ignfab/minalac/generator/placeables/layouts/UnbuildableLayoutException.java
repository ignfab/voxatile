package com.ignfab.minalac.generator.placeables.layouts;

/**
 * An exception thrown when a layout builder cannot build.
 * <p>
 * This may happen when building or as soon as the situation is detected.
 */
public class UnbuildableLayoutException extends Exception {

    /**
     * Creates a new unbuildable exception.
     * @param message the error message
     */
    public UnbuildableLayoutException(String message) {
        super(message);
    }

    /**
     * Creates a new unbuildable exception.
     * @param cause the cause of this error
     */
    public UnbuildableLayoutException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new unbuildable exception.
     * @param message the error message
     * @param cause the cause of this error
     */
    public UnbuildableLayoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

