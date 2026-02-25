package com.ignfab.minalac.generator.parameters.tasks.tile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

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
        params = new HeightmapStatsTaskParams(TestingHeightmapParams.VALID, compute);
        assertDoesNotThrow(params::validate);

        params = new HeightmapStatsTaskParams(TestingHeightmapParams.VALID, compute);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new HeightmapStatsTaskParams(TestingHeightmapParams.INVALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        // Nothing specified to compute
        compute = new HeightmapStatsTaskParams.ComputeParams();
        params = new HeightmapStatsTaskParams(TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        compute = new HeightmapStatsTaskParams.ComputeParams();
        compute.maximum = "max";
        compute.minimum = "";
        params = new HeightmapStatsTaskParams(TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);

        compute = new HeightmapStatsTaskParams.ComputeParams();
        compute.maximum = "";
        compute.minimum = "min";
        params = new HeightmapStatsTaskParams(TestingHeightmapParams.VALID, compute);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testDeserialization() {
        HeightmapStatsTaskParams params;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(HeightmapStatsTaskParams.class, "computeHeightmapStats"));

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(HeightmapStatsTaskParams.class, """
        type: computeHeightmapStats
        models:
          type: building
        heightmap: ground
        compute:
          maximum: max
        """, mapper));
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
        """, mapper));
        assertEquals("building", params.models.type);
        assertNotNull(params.heightmap);
        assertEquals("max", params.compute.maximum);
        assertEquals("min", params.compute.minimum);
    }
}
