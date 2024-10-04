package com.ignfab.minalac.generator.models;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A testing model with only metadata and assertions.
 */
public class TestingModel extends Model {
    public TestingModel() {}

    public TestingModel(Map<String, Object> metadata) {
        metadata.forEach(this::setMetadata);
    }

    private static String prefix(String message) {
        return message == null ? "" : message + " ===> ";
    }

    public void assertMetadata(String name, Object value) {
        assertMetadata(name, value, null);
    }

    public void assertMetadata(String name, Object value, String message) {
        assertEquals(getMetadata(name), value, prefix(message) + "Metadata mismatch with name: " + name);
    }

    public void assertMetadataAbsent(String name) {
        assertMetadataAbsent(name, null);
    }

    public void assertMetadataAbsent(String name, String message) {
        assertFalse(hasMetadata(name), prefix(message) + "Unexpected metadata with name: " + name);
    }

    @Override
    public String salt() {
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }
}
