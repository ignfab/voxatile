package com.ignfab.minalac.generator.parameters.processors.post;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetadataCopyPostProcessorParamsTest {
    private static ObjectMapper mapper;

    @BeforeAll
    public static void init() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MetadataCopyPostProcessorParams.class, "copy"));
    }

    @Test
    public void testCreate() {
        MetadataCopyPostProcessorParams params = new MetadataCopyPostProcessorParams("toto", "tata");
        assertDoesNotThrow(params::create);
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

        MetadataCopyPostProcessorParams requiredParams = assertDoesNotThrow(() -> mapper.readValue(requiredFields, MetadataCopyPostProcessorParams.class));
        assertEquals("hauteur", requiredParams.metadata);
        assertEquals("height", requiredParams.to);
        assertFalse(requiredParams.abortIfMetadataIsAbsent);
        assertFalse(requiredParams.keepExisting);

        String requiredAndOptionalFields = """
            type: copy
            metadata: toto
            to: tata
            keepExisting: yes
            abortIfMetadataIsAbsent: yes
        """;

        MetadataCopyPostProcessorParams requiredAndOptionalParams = assertDoesNotThrow(() -> mapper.readValue(requiredAndOptionalFields, MetadataCopyPostProcessorParams.class));
        assertEquals("toto", requiredAndOptionalParams.metadata);
        assertEquals("tata", requiredAndOptionalParams.to);
        assertTrue(requiredAndOptionalParams.abortIfMetadataIsAbsent);
        assertTrue(requiredAndOptionalParams.keepExisting);
    }
}
