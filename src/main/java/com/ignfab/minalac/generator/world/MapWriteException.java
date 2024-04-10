package com.ignfab.minalac.generator.world;

public class MapWriteException extends Exception {

    public MapWriteException(String message) {
        super("[MapWriteException] " + message);
    }
}