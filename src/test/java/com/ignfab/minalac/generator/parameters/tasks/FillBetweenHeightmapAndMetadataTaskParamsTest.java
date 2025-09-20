package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.values.MetadataValueParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class FillBetweenHeightmapAndMetadataTaskParamsTest {
    @Test
    void testValidate() {
        FillBetweenHeightmapAndValueTaskParams params;

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new MetadataValueParams("altitude")
        );
        params.placeAbove = new TestingVoxelParams("above");
        params.placeBelow = new TestingVoxelParams("below");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new MetadataValueParams("altitude")
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.INVALID,
            TestingHeightmapParams.VALID,
            new MetadataValueParams("altitude")
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.INVALID,
            new MetadataValueParams("altitude")
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new MetadataValueParams("")
        );
        params.placeAbove = new TestingVoxelParams("above");

        assertThrows(IllegalArgumentException.class, params::validate);
        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            new MetadataValueParams("altitude")
        );
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(FillBetweenHeightmapAndValueTaskParams.class, "filling"));

        FillBetweenHeightmapAndValueTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FillBetweenHeightmapAndValueTaskParams.class, """
        type: filling
        models:
          type: models
        heightmap: heightmap
        altitudeValue: altitude
        placeAbove: voxelA
        placeBelow: voxelB
        """, mapper));
        assertEquals("models", params.models.type);
        assertEquals("altitude", assertInstanceOf(MetadataValueParams.class, params.altitudeValue).metadata);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelParams.class, params.placeAbove).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelParams.class, params.placeBelow).name);
    }
}
