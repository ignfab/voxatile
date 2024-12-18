package com.ignfab.minalac.generator.parameters.models.filters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
        assertDoesNotThrow(new ModelFilterHasMetadataParams(List.of("a"))::create);
        assertDoesNotThrow(new ModelFilterHasMetadataParams(List.of("a", "b"))::create);
    }
}
