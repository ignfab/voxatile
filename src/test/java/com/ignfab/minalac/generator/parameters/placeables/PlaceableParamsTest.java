package com.ignfab.minalac.generator.parameters.placeables;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.structures.StackStructureParams;

public class PlaceableParamsTest {

    @Test
    @DisplayName("Test placeable deserialization using shortcut")
    public void testPlaceableParamsDeserializerShortcut() throws JsonMappingException, JsonProcessingException {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "test"));
        TestingVoxelTypeParams voxelTypeParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(voxelTypeParams.name, "test");
    }

    @Test
    @DisplayName("Test placeable deserialization using shortcut without existing shortcut")
    public void testPlaceableParamsDeserializerNoShortcut() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, TestingVoxelTypeParams.class, null);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "test", format));
    }

    @Test
    @DisplayName("Test placeable deserialization using default")
    public void testPlaceableParamsDeserializerDefault() throws JsonMappingException, JsonProcessingException {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "name: tata"));
        TestingVoxelTypeParams voxelTypeParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(voxelTypeParams.name, "tata");
    }

    @Test
    @DisplayName("Test placeable deserialization using default without existing default")
    public void testPlaceableParamsDeserializerNoDefault() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, null, TestingVoxelTypeParams::new);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "name: tata", format));
    }

    @Test
    @DisplayName("Test placeable deserialization using typed params")
    public void testPlaceableParamsDeserializerTyped() throws JsonMappingException, JsonProcessingException {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
                type: stack
                layers:
                  - material: toto
                  - material: titi
                """));

        StackStructureParams structureParams = assertInstanceOf(StackStructureParams.class, params);
        assertEquals(2, structureParams.layers.size());
    }
}
