package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.parameters.ValueParser;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataDefaultPostProcessorParamsTest {
    @Test
    public void testCreate() {
        MetadataDefaultPostProcessorParams params1 = new MetadataDefaultPostProcessorParams("toto", "toto", ValueParser.INTEGER);
        assertThrows(NumberFormatException.class, () -> params1.create(TestingGeneration.UNUSED));

        MetadataDefaultPostProcessorParams params2 = new MetadataDefaultPostProcessorParams("toto", "5", ValueParser.INTEGER);
        assertDoesNotThrow(() -> params2.create(TestingGeneration.UNUSED));
    }

    @Test
    public void testValidate() {
        assertThrows(IllegalArgumentException.class, new MetadataDefaultPostProcessorParams("", "5", ValueParser.INTEGER)::validate);
        assertDoesNotThrow(new MetadataDefaultPostProcessorParams("toto", "5", ValueParser.INTEGER)::validate);
    }
}
