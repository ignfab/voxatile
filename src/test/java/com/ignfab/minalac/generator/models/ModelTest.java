package com.ignfab.minalac.generator.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    private TestingModel model;

    @BeforeEach
    void init() {
        model = new TestingModel();
    }

    @AfterEach
    void tearDown() {
        model = null;
    }

    @Test
    @DisplayName("\"setMetadata\" should store a value and remove it when set to null")
    void testSetMetadata() {
        model.setMetadata("test", "dummy");
        model.assertMetadata("test", "dummy");

        // Verify that "null" value removes metadata
        model.setMetadata("test", null);
        model.assertMetadataAbsent("test");
        model.assertMetadata("test", null);
    }

    @Test
    @DisplayName("\"hasMetadata\" should return true when metadata exists and false otherwise")
    void testHasMetadata() {
        model.setMetadata("test", "dummy");
        assertTrue(model.hasMetadata("test"));

        model.assertMetadataAbsent("invalid");
    }
}
