package com.ignfab.minalac.generator.parameters.placeables;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceableParamsTest {

    // Voxel deserialization

    @Test
    @DisplayName("Test voxel deserialization")
    public void testPlaceableParamsDeserializerShortcut() {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "test"));
        TestingVoxelParams voxelParams = assertInstanceOf(TestingVoxelParams.class, params);
        assertEquals("test", voxelParams.name);
    }

    @Test
    @DisplayName("Test voxel deserialization using shortcut without existing shortcut")
    public void testPlaceableParamsDeserializerNoShortcut() {

        OutputFormat format = new OutputFormat(null, TestingVoxelParams.class, null);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "test", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using default")
    public void testPlaceableParamsDeserializerDefault() {

        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, "name: tata"));
        TestingVoxelParams voxelParams = assertInstanceOf(TestingVoxelParams.class, params);
        assertEquals("tata", voxelParams.name);
    }

    @Test
    @DisplayName("Test voxel deserialization using default without existing default")
    public void testPlaceableParamsDeserializerNoDefault() {

        OutputFormat format = new OutputFormat(null, null, TestingVoxelParams::new);
        assertThrows(IllegalArgumentException.class, () -> ParamsTester.deserialize(PlaceableParams.class, "name: tata", format));
    }

    @Test
    @DisplayName("Test voxel deserialization using `voxel:` field")
    public void testPlaceableParamsVoxelDeserialization() {
        PlaceableParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            voxel:
                name: tata
        """));
        TestingVoxelParams voxelParams = assertInstanceOf(TestingVoxelParams.class, params);
        assertEquals("tata", voxelParams.name);
    }

    // Combined deserialization

    @Test
    @DisplayName("Test placeable deserialization using combined params")
    public void testPlaceableParamsDeserializerCombined() {

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
    public void testPlaceableParamsStructureDeserialization() {
        PlaceableParams params;
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(PlaceableParams.class, """
            structure:
              - at: [0, 0, 0]
                put: A
        """));

        assertInstanceOf(PlaceableStructureParams.class, params);
    }
}
