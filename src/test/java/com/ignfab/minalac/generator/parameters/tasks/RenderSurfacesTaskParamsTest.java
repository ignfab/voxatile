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
        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.INVALID, TestingPlaceableParams.VALID);
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID,  TestingPlaceableParams.INVALID);
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderSurfacesTaskParams(TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);
    }
}
