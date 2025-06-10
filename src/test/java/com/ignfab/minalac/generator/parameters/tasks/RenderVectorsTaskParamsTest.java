package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderVectorsTaskParamsTest {
    @Test
    public void testValidate() {
        RenderVectorsTaskParams params;

        // Test required arguments
        params = new RenderVectorsTaskParams(TestingModelSelectionParams.INVALID, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.INVALID);
        params.place = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        // Test optional arguments
        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        params.inside = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.inside = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        // Test incompatible arguments
        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        params.borders = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        params.inside = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        // Test invalid arguments
        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.inside = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderVectorsTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

    }
}
