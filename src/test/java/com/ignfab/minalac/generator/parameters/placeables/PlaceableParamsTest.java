package com.ignfab.minalac.generator.parameters.placeables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.NoVoxelParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceableParamsTest {

    // Voxel deserialization

    @Test
    @DisplayName("Test voxel deserialization")
    public void testPlaceableParamsDeserializerShortcut() throws JsonMappingException, JsonProcessingException {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "test"));
        TestingVoxelTypeParams voxelTypeParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(voxelTypeParams.name, "test");
    }

    @Test
    @DisplayName("Test voxel deserialization using shortcut without existing shortcut")
    public void testPlaceableParamsDeserializerNoShortcut() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, TestingVoxelTypeParams.class, null);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "test", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using default")
    public void testPlaceableParamsDeserializerDefault() throws JsonMappingException, JsonProcessingException {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "name: tata"));
        TestingVoxelTypeParams voxelTypeParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(voxelTypeParams.name, "tata");
    }

    @Test
    @DisplayName("Test voxel deserialization using default without existing default")
    public void testPlaceableParamsDeserializerNoDefault() throws JsonMappingException, JsonProcessingException {

        OutputFormat format = new OutputFormat(null, null, TestingVoxelTypeParams::new);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "name: tata", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using `voxel:` field")
    public void testPlaceableParamsVoxelDeserialization() {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            voxel:
                name: tata
        """));
        TestingVoxelTypeParams voxelTypeParams = assertInstanceOf(TestingVoxelTypeParams.class, params);
        assertEquals(voxelTypeParams.name, "tata");
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
        assertInstanceOf(NoVoxelParams.class, params);

        // Shortcut test
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "nothing"));
        assertInstanceOf(NoVoxelParams.class, params);
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
