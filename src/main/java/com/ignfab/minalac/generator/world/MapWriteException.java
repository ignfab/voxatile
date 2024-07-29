package com.ignfab.minalac.generator.world;

/**
 * Exception thrown if a problem occurs when saving a {@link VoxelWorld}.
 *
 * @see VoxelWorld#save(File)
 */
public class MapWriteException extends Exception {
    /**
     * Creates a new {@code MapWriteException}.
     *
     * @param message the message explaining the problem
     */
    public MapWriteException(String message) {
        this(message, null);
    }

    /**
     * Creates a new {@code MapWriteException}.
     *
     * @param message the message explaining the problem
     * @param cause   the throwable that caused the problem
     */
    public MapWriteException(String message, Throwable cause) {
        super("[MapWriteException] " + message, cause);
    }

    /**
     * Creates a new {@code MapWriteException}.
     *
     * @param cause the throwable that caused the problem
     */
    public MapWriteException(Throwable cause) {
        super(cause);
    }
}
