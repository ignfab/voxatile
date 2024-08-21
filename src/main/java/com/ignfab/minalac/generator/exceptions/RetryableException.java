package com.ignfab.minalac.generator.exceptions;

/**
 * Exception thrown when something goes wrong but retrying may solve the problem.
 * The code receiving this error can decide to retry the current action,
 * or completely abort and cause a fatal error (e.g. after a set number of tries).
 * It is up to the receiver whether to wait before trying again.
 * <p>
 * An example of when this issue could happen is when fetching remote content,
 * such as data from an HTTP server. If the network goes down for a moment,
 * the operation would fail, but can succeed on the second try.
 */
public class RetryableException extends Exception {
    /**
     * Creates a new retryable exception.
     * @param message the error message
     */
    public RetryableException(String message) {
        super(message);
    }

    /**
     * Creates a new retryable exception.
     * @param cause the cause of this error
     */
    public RetryableException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new retryable exception.
     * @param message the error message
     * @param cause the cause of this error
     */
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
