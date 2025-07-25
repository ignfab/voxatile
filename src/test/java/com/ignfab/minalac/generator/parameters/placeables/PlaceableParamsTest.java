package com.ignfab.minalac.generator.parameters.placeables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceableParamsTest {

    // Voxel deserialization

    @Test
    @DisplayName("Test voxel deserialization")
    public void testPlaceableParamsDeserializerShortcut() throws JsonMappingException, JsonProcessingException {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "test"));
        TestingPlaceableParams voxelParams = assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(voxelParams.name, "test");
    }

    @Test
    @DisplayName("Test voxel deserialization using shortcut without existing shortcut")
    public void testPlaceableParamsDeserializerNoShortcut() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, TestingPlaceableParams.class, null);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "test", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using default")
    public void testPlaceableParamsDeserializerDefault() throws JsonMappingException, JsonProcessingException {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "name: tata"));
        TestingPlaceableParams voxelParams = assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(voxelParams.name, "tata");
    }

    @Test
    @DisplayName("Test voxel deserialization using default without existing default")
    public void testPlaceableParamsDeserializerNoDefault() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, null, TestingPlaceableParams::new);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "name: tata", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using `voxel:` field")
    public void testPlaceableParamsVoxelDeserialization() {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            voxel:
                name: tata
        """));
        TestingPlaceableParams voxelParams = assertInstanceOf(TestingPlaceableParams.class, params);
        assertEquals(voxelParams.name, "tata");
    }

    // Combined deserialization

    @Test
    @DisplayName("Test placeable deserialization using combined params")
    public void testPlaceableParamsDeserializerCombined() throws JsonMappingException, JsonProcessingException {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "[ titi, toto, tata ]"));

        CombinedPlaceableParams combinedParams = assertInstanceOf(CombinedPlaceableParams.class, params);
        assertEquals(3, combinedParams.placeableParams.size());
    }

    // NoVoxel deserialization

    @Test
    @DisplayName("Test nothing deserialization")
    public void testPlaceableParamsNothingDeserialization() {
        PlaceableParams params;

        // Canonical test
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            nothing:
        """));
        assertInstanceOf(NothingParams.class, params);

        // Shortcut test
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "nothing"));
        assertInstanceOf(NothingParams.class, params);
    }

    // Structure deserialization

    @Test
    @DisplayName("Test structure deserialization")
    public void testPlaceableParamsStructureDeserialization() throws JsonMappingException, JsonProcessingException {
        PlaceableParams params;
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            structure:
              - at: [0, 0, 0]
                put: A
        """));

        assertInstanceOf(PlaceableStructureParams.class, params);
    }
}
