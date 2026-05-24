package com.ignfab.minalac.generator.parameters.models.filters;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterHasMetadataParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new ModelFilterHasMetadataParams(List.of("a")));
        assertDoesNotThrow(() -> new ModelFilterHasMetadataParams(List.of("a", "b")));
        assertDoesNotThrow(() -> new ModelFilterHasMetadataParams(List.of()));
    }

    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new ModelFilterHasMetadataParams(List.of("a", ""))::validate);
        assertDoesNotThrow(new ModelFilterHasMetadataParams(List.of("a", "b"))::validate);
        assertDoesNotThrow(new ModelFilterHasMetadataParams(List.of("a"))::validate);
        assertThrows(IllegalArgumentException.class, new ModelFilterHasMetadataParams(List.of())::validate);
    }

    @Test
    public void testCreate() {
        ModelFilterHasMetadataParams params1 = new ModelFilterHasMetadataParams(List.of("a"));
        assertDoesNotThrow(() -> params1.create(TestingGeneration.UNUSED));

        ModelFilterHasMetadataParams params2 = new ModelFilterHasMetadataParams(List.of("a", "b"));
        assertDoesNotThrow(() -> params2.create(TestingGeneration.UNUSED));
    }
}
