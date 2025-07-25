package com.ignfab.minalac.generator.parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;


import static org.junit.jupiter.api.Assertions.*;

public class OutputFormatTest {

    @Test
    @DisplayName("Simple constructor test")
    public void testConstructor() {
        assertDoesNotThrow(() -> new OutputFormat(
            () -> null,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        ));
    }

    @Test
    @DisplayName("Test createVoxelParams using shortcut")
    public void testCreateVoxelParamsShortcut() {
        OutputFormat format = new OutputFormat(
            () -> null,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelParams("test"));
        TestingPlaceableParams testingParams = assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelParams using default class")
    public void testCreateVoxelParamsDefault() throws JsonProcessingException {
        OutputFormat format = new OutputFormat(
            () -> null,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("{ \"name\": \"test\" }", JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelParams(node, mapper));
        TestingPlaceableParams testingParams = assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelParams with null default and shortcut")
    public void testCreateVoxelParamsNull() throws JsonProcessingException {
        OutputFormat format = assertDoesNotThrow(() -> new OutputFormat(
            () -> null,
            null,
            null
        ));

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("{ \"toto\": \"tata\" }", JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> format.createVoxelParams("test"));
        assertThrows(IllegalArgumentException.class, () -> format.createVoxelParams(node, mapper));
    }
}
