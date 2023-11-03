package com.ignfab.minalac.generator;

public class MapWriteException extends Exception {

    public MapWriteException(String message) {
        super("[MapWriteException] " + message);
    }
}