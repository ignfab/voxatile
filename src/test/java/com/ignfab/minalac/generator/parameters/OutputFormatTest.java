package com.ignfab.minalac.generator.parameters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;

public class OutputFormatTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> {
            new OutputFormat(
                TestingPlaceableParams::worldCreator,
                TestingPlaceableParams.class,
                TestingPlaceableParams::new
            );
        });
    }

    @Test
    public void testCreateVoxelTypeParamsShortcut() {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelTypeParams("test"));
        assertNotNull(params);
        assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(((TestingPlaceableParams) params).name, "test");
        assertNull(((TestingPlaceableParams) params).param);
    }

    @Test
    public void testCreateVoxelTypeParamsDefault() throws JsonProcessingException {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("""
            { "name": "tata", "param": "titi" }
            """, JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> format.createVoxelTypeParams(node, mapper));
        assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(((TestingPlaceableParams) params).name, "tata");
        assertEquals(((TestingPlaceableParams) params).param, "titi");
    }

    @Test
    public void testCreateVoxelTypeParamsNull() throws JsonProcessingException {
        OutputFormat format = assertDoesNotThrow(() -> new OutputFormat(
            null,
            null,
            null
        ));

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("""
            { "toto": "tata" }
            """, JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> format.createVoxelTypeParams("test"));
        assertThrows(IllegalArgumentException.class, () -> format.createVoxelTypeParams(node, mapper));
    }
}
