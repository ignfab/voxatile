package com.ignfab.minalac.generator.parameters.processors.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class MetadataDefaultPostProcessorParamsTest {
    @Test
    public void testCreate() {
        MetadataDefaultPostProcessorParams params;
        params = new MetadataDefaultPostProcessorParams("toto", "toto", "integer");
        assertThrows(NumberFormatException.class, params::create);

        params = new MetadataDefaultPostProcessorParams("toto", "5", "integer");
        assertDoesNotThrow(params::create);
    }

    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new MetadataDefaultPostProcessorParams("", "5", "integer")::validate);
        assertThrows(IllegalArgumentException.class, new MetadataDefaultPostProcessorParams("toto", "5", "invalid")::validate);
        assertDoesNotThrow(new MetadataDefaultPostProcessorParams("toto", "5", "integer")::validate);
    }
}
