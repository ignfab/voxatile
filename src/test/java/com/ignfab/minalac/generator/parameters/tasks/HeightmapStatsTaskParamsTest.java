package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapStatsTaskParamsTest {
    @Test
    public void testValidate() {
        HeightmapStatsTaskParams.ComputeParams compute;
        HeightmapStatsTaskParams params;

        compute = new HeightmapStatsTaskParams.ComputeParams();
        compute.maximum = "max";
        compute.minimum = "min";
        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID, compute);
        assertDoesNotThrow(params::validate);

        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.INVALID, TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.INVALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        // Nothing specified to compute
        compute = new HeightmapStatsTaskParams.ComputeParams();
        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        compute = new HeightmapStatsTaskParams.ComputeParams();
        compute.maximum = "max";
        compute.minimum = "";
        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        compute = new HeightmapStatsTaskParams.ComputeParams();
        compute.maximum = "";
        compute.minimum = "min";
        params = new HeightmapStatsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testDeserialization() {
        HeightmapStatsTaskParams params;

        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("computeHeightmapStats", HeightmapStatsTaskParams.class);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(HeightmapStatsTaskParams.class, """
        type: computeHeightmapStats
        models:
          type: building
        heightmap: ground
        compute:
          maximum: max
        """, builder));
        assertEquals("building", params.models.type);
        assertNotNull(params.heightmap);
        assertEquals("max", params.compute.maximum);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(HeightmapStatsTaskParams.class, """
        type: computeHeightmapStats
        models:
          type: building
        heightmap: ground
        compute:
          maximum: max
          minimum: min
        """, builder));
        assertEquals("building", params.models.type);
        assertNotNull(params.heightmap);
        assertEquals("max", params.compute.maximum);
        assertEquals("min", params.compute.minimum);
    }
}
