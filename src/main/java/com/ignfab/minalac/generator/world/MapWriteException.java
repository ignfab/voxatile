package com.ignfab.minalac.generator.world;

public class MapWriteException extends Exception {
    public MapWriteException(String message) {
        this(message, null);
    }

    public MapWriteException(String message, Throwable cause) {
        super("[MapWriteException] " + message, cause);
    }

    public MapWriteException(Throwable cause) {
        super(cause);
    }
}
