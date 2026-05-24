package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class MetadataCopyPostProcessorParamsTest {
    private static final ObjectMapper MAPPER = ParamsTester.mapperWithParams("copy", MetadataCopyPostProcessorParams.class);

    @Test
    public void testCreate() {
        MetadataCopyPostProcessorParams params = new MetadataCopyPostProcessorParams("toto", "tata");
        assertDoesNotThrow(() -> params.create(TestingGeneration.UNUSED));
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new MetadataCopyPostProcessorParams("toto", "tata")::validate);
        assertThrows(IllegalArgumentException.class, new MetadataCopyPostProcessorParams("toto", "")::validate);
        assertThrows(IllegalArgumentException.class, new MetadataCopyPostProcessorParams("", "tata")::validate);
    }

    @Test
    public void testDeserialization() {
        String requiredFields = """
            type: copy
            metadata: hauteur
            to: height
        """;

        MetadataCopyPostProcessorParams requiredParams = assertDoesNotThrow(() -> MAPPER.readValue(requiredFields, MetadataCopyPostProcessorParams.class));
        assertEquals("hauteur", requiredParams.metadata);
        assertEquals("height", requiredParams.to);
        assertFalse(requiredParams.abortIfMetadataIsAbsent);
        assertFalse(requiredParams.keepExisting);

        String requiredAndOptionalFields = """
            type: copy
            metadata: toto
            to: tata
            keepExisting: true
            abortIfMetadataIsAbsent: true
        """;

        MetadataCopyPostProcessorParams requiredAndOptionalParams = assertDoesNotThrow(() -> MAPPER.readValue(requiredAndOptionalFields, MetadataCopyPostProcessorParams.class));
        assertEquals("toto", requiredAndOptionalParams.metadata);
        assertEquals("tata", requiredAndOptionalParams.to);
        assertTrue(requiredAndOptionalParams.abortIfMetadataIsAbsent);
        assertTrue(requiredAndOptionalParams.keepExisting);
    }
}
