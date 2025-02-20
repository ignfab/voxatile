package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingVoxelTypeParams;

import static org.junit.jupiter.api.Assertions.*;

public class OutputFormatTest {

    @Test
    @DisplayName("Simple constructor test")
    public void testConstructor() {
        assertDoesNotThrow(() -> new OutputFormat(
            () -> null,
            TestingVoxelTypeParams.class,
            TestingVoxelTypeParams::new
        ));
    }

    @Test
    @DisplayName("Test createVoxelTypeParams using shortcut")
    public void testCreateVoxelTypeParamsShortcut() {
        OutputFormat format = new OutputFormat(
            () -> null,
            TestingVoxelTypeParams.class,
            TestingVoxelTypeParams::new
        );

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelTypeParams("test"));
        TestingVoxelTypeParams testingParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelTypeParams using default class")
    public void testCreateVoxelTypeParamsDefault() throws JsonProcessingException {
        OutputFormat format = new OutputFormat(
            () -> null,
            TestingVoxelTypeParams.class,
            TestingVoxelTypeParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("{ \"name\": \"test\" }", JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelTypeParams(node, mapper));
        TestingVoxelTypeParams testingParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelTypeParams with null default and shortcut")
    public void testCreateVoxelTypeParamsNull() throws JsonProcessingException {
        OutputFormat format = assertDoesNotThrow(() -> new OutputFormat(
            () -> null,
            null,
            null
        ));

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("{ \"toto\": \"tata\" }", JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> format.createVoxelTypeParams("test"));
        assertThrows(IllegalArgumentException.class, () -> format.createVoxelTypeParams(node, mapper));
    }
}
