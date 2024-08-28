package com.ignfab.minalac.generator.parameters;

/**
 * Exception thrown if a problem occurs during parsing.
 */
public class ParseException extends Exception {

    /**
     * Creates a ParseException.
     *
     * @param message Message explaining the problem
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a ParseException.
     *
     * @param message Message explaining the problem
     * @param cause Throwable that caused the problem
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a ParseException.
     *
     * @param cause Throwable that caused the problem
     */
    public ParseException(Throwable cause) {
        super(cause);
    }
}
