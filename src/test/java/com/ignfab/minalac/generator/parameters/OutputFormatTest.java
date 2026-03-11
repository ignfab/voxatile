package com.ignfab.minalac.generator.parameters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class OutputFormatTest {

    @Test
    @DisplayName("Simple constructor test")
    public void testConstructor() {
        assertDoesNotThrow(() -> new OutputFormat(
            (destination) -> null,
            TestingVoxelParams.class,
            TestingVoxelParams::new
        ));
    }

    @Test
    @DisplayName("Test createVoxelParams using shortcut")
    public void testCreateVoxelParamsShortcut() {
        OutputFormat format = new OutputFormat(
            (destination) -> null,
            TestingVoxelParams.class,
            TestingVoxelParams::new
        );

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelParams("test"));
        TestingVoxelParams testingParams = assertInstanceOf(TestingVoxelParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelParams using default class")
    public void testCreateVoxelParamsDefault() throws JacksonException {
        OutputFormat format = new OutputFormat(
            (destination) -> null,
            TestingVoxelParams.class,
            TestingVoxelParams::new
        );

        MapperBuilder<?, ?> builder = YAMLMapper.builder();
        format.registerPlaceableDeserializer(builder);
        ObjectMapper mapper = builder.build();

        JsonNode node = mapper.readValue("{ \"name\": \"test\" }", JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelParams(node, mapper._deserializationContext()));
        TestingVoxelParams testingParams = assertInstanceOf(TestingVoxelParams.class, params);
        assertEquals(testingParams.name, "test");
    }

    @Test
    @DisplayName("Test createVoxelParams with null default and shortcut")
    public void testCreateVoxelParamsNull() throws JacksonException {
        OutputFormat format = assertDoesNotThrow(() -> new OutputFormat(
            (destination) -> null,
            null,
            null
        ));

        MapperBuilder<?, ?> builder = YAMLMapper.builder();
        format.registerPlaceableDeserializer(builder);
        ObjectMapper mapper = builder.build();

        JsonNode node = mapper.readValue("{ \"toto\": \"tata\" }", JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> format.createVoxelParams("test"));
        assertThrows(IllegalArgumentException.class, () -> format.createVoxelParams(node, mapper._deserializationContext()));
    }
}
