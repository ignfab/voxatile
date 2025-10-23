package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderSurfacesTaskParamsTest {
    @Test
    public void testValidate() {
        RenderSurfacesTaskParams params;

        // Test required arguments
        params = new RenderSurfacesTaskParams(TestingModelSelectionParams.INVALID, TestingPlaceableParams.VALID);
        params.heightmap = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingModelSelectionParams.VALID, TestingPlaceableParams.VALID);
        params.heightmap = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingModelSelectionParams.VALID, TestingPlaceableParams.INVALID);
        params.heightmap = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingModelSelectionParams.VALID, TestingPlaceableParams.VALID);
        params.heightmap = TestingHeightmapParams.VALID;
        assertDoesNotThrow(params::validate);
    }
}
