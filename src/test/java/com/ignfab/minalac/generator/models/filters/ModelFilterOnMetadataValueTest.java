package com.ignfab.minalac.generator.models.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterOnMetadataValueTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterOnMetadataValue<>(Integer.class, "i", i -> i == 1));
    }
}
