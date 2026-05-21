package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.values.MetadataValueParams;
import com.ignfab.minalac.generator.parameters.models.values.TestingModelValueParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class FillBetweenHeightmapAndValueTaskParamsTest {
    @Test
    void testValidate() {
        FillBetweenHeightmapAndValueTaskParams params;

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.VALID,
            TestingModelValueParams.VALID
        );
        params.placeAbove = new TestingVoxelParams("above");
        params.placeBelow = new TestingVoxelParams("below");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.VALID,
            TestingModelValueParams.VALID
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertDoesNotThrow(params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.VALID,
            TestingModelValueParams.VALID
        );
        params.placeAbove = new TestingVoxelParams("above");
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.INVALID,
            TestingModelValueParams.VALID
        );
        params.placeAbove = new TestingVoxelParams("above");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.VALID,
            TestingModelValueParams.INVALID
        );
        params.placeAbove = new TestingVoxelParams("above");

        assertThrows(IllegalArgumentException.class, params::validate);
        params = new FillBetweenHeightmapAndValueTaskParams(
            TestingHeightmapParams.VALID,
            TestingModelValueParams.VALID
        );
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    void testDeserialization() {
        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("filling", FillBetweenHeightmapAndValueTaskParams.class);

        FillBetweenHeightmapAndValueTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(FillBetweenHeightmapAndValueTaskParams.class, """
        type: filling
        models:
          type: models
        heightmap: heightmap
        altitudeValue: altitude
        placeAbove: voxelA
        placeBelow: voxelB
        """, builder));
        assertEquals("models", params.models.type);
        assertEquals("altitude", assertInstanceOf(MetadataValueParams.class, params.altitudeValue).metadata);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelParams.class, params.placeAbove).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelParams.class, params.placeBelow).name);
    }
}
