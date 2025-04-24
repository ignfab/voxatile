package com.ignfab.minalac.generator.models;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A testing model with only metadata and assertions.
 */
public class TestingModel extends ModelImpl {
    private final String name;

    public TestingModel(String name, Map<String, Object> metadata) {
        this.name = name;
        metadata.forEach(this::setMetadata);
    }

    public TestingModel(Map<String, Object> metadata) {
        this(null, metadata);
    }

    public TestingModel(String name) {
        this(name, Map.of());
    }

    public TestingModel() {
        this(null, Map.of());
    }

    private static String prefix(String message) {
        return message == null ? "" : message + " ==> ";
    }

    public void assertMetadata(String name, Object expectedValue) {
        assertMetadata(name, expectedValue, null);
    }

    public void assertMetadata(String name, Object expectedValue, String message) {
        assertEquals(expectedValue, getMetadata(name), prefix(message) + "model <%s> has wrong metadata with name <%s>".formatted(this, name));
    }

    public void assertMetadataPresent(String name) {
        assertMetadataPresent(name, null);
    }

    public void assertMetadataPresent(String name, String message) {
        assertTrue(hasMetadata(name), prefix(message) + "model <%s> is missing metadata with name <%s>".formatted(this, name));
    }

    public void assertMetadataAbsent(String name) {
        assertMetadataAbsent(name, null);
    }

    public void assertMetadataAbsent(String name, String message) {
        assertFalse(hasMetadata(name), prefix(message) + "model <%s> has metadata with name <%s>".formatted(this, name));
    }

    @Override
    public String salt() {
        throw new UnsupportedOperationException("Unimplemented method 'salt'");
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : "%s(name=%s)".formatted(getClass().getSimpleName(), name);
    }

    public static final class Subclass extends TestingModel {}
}
