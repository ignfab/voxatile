package com.ignfab.minalac.generator.parameters.placeables;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ignfab.minalac.generator.parameters.OutputFormat;

public class PlaceableParamsTest {

    @Test
    public void testPlaceableParamsDeserializerShortcut() throws JsonMappingException, JsonProcessingException {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("\"test\"", JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> mapper.treeToValue(node, PlaceableParams.class));
        assertNotNull(params);
        assertInstanceOf(TestingPlaceableParams.class, params);

        assertEquals(((TestingPlaceableParams) params).name, "test");
        assertNull(((TestingPlaceableParams) params).param);
    }

    @Test
    public void testPlaceableParamsDeserializerNoShortcut() throws JsonMappingException, JsonProcessingException {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            TestingPlaceableParams.class,
            null
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("\"test\"", JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> mapper.treeToValue(node, PlaceableParams.class));
    }

    @Test
    public void testPlaceableParamsDeserializerDefault() throws JsonMappingException, JsonProcessingException {
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

        PlaceableParams params = assertDoesNotThrow(() -> mapper.treeToValue(node, PlaceableParams.class));
        assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(((TestingPlaceableParams) params).name, "tata");
        assertEquals(((TestingPlaceableParams) params).param, "titi");
    }

    @Test
    public void testPlaceableParamsDeserializerNoDefault() throws JsonMappingException, JsonProcessingException {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            null,
            TestingPlaceableParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        JsonNode node = mapper.readValue("""
            { "name": "tata", "param": "titi" }
            """, JsonNode.class);

        assertThrows(IllegalArgumentException.class, () -> mapper.treeToValue(node, PlaceableParams.class));
    }

    @Test
    public void testPlaceableParamsDeserializerTyped() throws JsonMappingException, JsonProcessingException {
        OutputFormat format = new OutputFormat(
            TestingPlaceableParams::worldCreator,
            TestingPlaceableParams.class,
            TestingPlaceableParams::new
        );

        ObjectMapper mapper = new ObjectMapper();
        format.registerPlaceableDeserializer(mapper);

        // TODO: Use another placeable type once we have one (a structure for example)
        JsonNode node = mapper.readValue("""
            { "type": "voxel", "name": "tata", "param": "titi" }
            """, JsonNode.class);

        PlaceableParams params = assertDoesNotThrow(() -> mapper.treeToValue(node, PlaceableParams.class));
        assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(((TestingPlaceableParams) params).name, "tata");
        assertEquals(((TestingPlaceableParams) params).param, "titi");
    }

}
