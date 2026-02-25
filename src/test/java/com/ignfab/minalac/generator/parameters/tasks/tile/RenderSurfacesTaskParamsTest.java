package com.ignfab.minalac.generator.parameters.tasks.tile;

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
        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.INVALID, TestingPlaceableParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID,  TestingPlaceableParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
