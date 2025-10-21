package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class FillBetweenHeightmapAndMetadataTaskParamsTest {
    @Test
    void testValidate() {
        FillBetweenHeightmapAndMetadataTaskParams params;

        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.VALID,
            "altitude"
        );
        params.models = TestingModelSelectionParams.VALID;
        params.placeAbove = new TestingVoxelParams("above");
        params.placeBelow = new TestingVoxelParams("below");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.VALID,
            "altitude"
        );
        params.models = TestingModelSelectionParams.VALID;
        params.placeAbove = new TestingVoxelParams("above");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.VALID,
            "altitude"
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertThrows(IllegalArgumentException.class, params::validate);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.INVALID,
            "altitude"
        );
        params.models = TestingModelSelectionParams.VALID;
        params.placeAbove = new TestingVoxelParams("above");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.VALID,
            ""
        );
        params.models = TestingModelSelectionParams.VALID;
        params.placeAbove = new TestingVoxelParams("above");

        assertThrows(IllegalArgumentException.class, params::validate);
        params = new FillBetweenHeightmapAndMetadataTaskParams(
            TestingHeightmapParams.VALID,
            "altitude"
        );
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(FillBetweenHeightmapAndMetadataTaskParams.class, "filling"));

        FillBetweenHeightmapAndMetadataTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FillBetweenHeightmapAndMetadataTaskParams.class, """
        type: filling
        models:
          type: models
        heightmap: heightmap
        altitudeMetadata: altitude
        placeAbove: voxelA
        placeBelow: voxelB
        """, mapper));
        assertEquals("models", params.models.type);
        assertEquals("altitude", params.altitudeMetadata);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelParams.class, params.placeAbove).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelParams.class, params.placeBelow).name);
    }
}
