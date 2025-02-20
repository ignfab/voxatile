package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ValueParser;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataDefaultPostProcessorParamsTest {
    @Test
    public void testCreate() {
        MetadataDefaultPostProcessorParams params;
        params = new MetadataDefaultPostProcessorParams("toto", "toto", ValueParser.INTEGER);
        assertThrows(NumberFormatException.class, params::create);

        params = new MetadataDefaultPostProcessorParams("toto", "5", ValueParser.INTEGER);
        assertDoesNotThrow(params::create);
    }

    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new MetadataDefaultPostProcessorParams("", "5", ValueParser.INTEGER)::validate);
        assertDoesNotThrow(new MetadataDefaultPostProcessorParams("toto", "5", ValueParser.INTEGER)::validate);
    }
}
