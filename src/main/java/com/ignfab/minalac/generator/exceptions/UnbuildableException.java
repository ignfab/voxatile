package com.ignfab.minalac.generator.exceptions;

/**
 * An exception thrown when a builder cannot build.
 */
public class UnbuildableException extends Exception {

    /**
     *
     */
    public UnbuildableException(String message) {
        super(message);
    }

    public UnbuildableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnbuildableException(Throwable cause) {
        super(cause);
    }
}
