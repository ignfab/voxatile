package com.ignfab.minalac.generator.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    private static final class DummyModel extends ModelImpl {
        @Override
        public String salt() {
            return "";
        }
    }

    private DummyModel model;

    @BeforeEach
    void init() {
        model = new DummyModel();
    }

    @AfterEach
    void tearDown() {
        model = null;
    }

    @Test
    @DisplayName("Test \"setMetadata\" method")
    void testSetMetadata() {
        model.setMetadata("test", "dummy");
        assertEquals("dummy", (String) model.getMetadata("test"));

        // Verify that "null" value removes metadata
        model.setMetadata("test", null);
        assertFalse(model.hasMetadata("test"));
        assertNull(model.getMetadata("test"));
    }

    @Test
    @DisplayName("Test if the \"hasMetadata\" method finds the metadata")
    void testHasMetadata() {
        model.setMetadata("test", "dummy");
        assertTrue(model.hasMetadata("test"));

        assertFalse(model.hasMetadata("invalid"));
    }
}
