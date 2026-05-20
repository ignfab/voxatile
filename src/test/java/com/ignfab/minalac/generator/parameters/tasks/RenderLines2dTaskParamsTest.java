package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLines2dTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLines2dTaskParams params;

        // Check any invalid members makes valid throw
        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.INVALID, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // All valid
        params = new RenderLines2dTaskParams(TestingPlaceableStructureParams.VALID, TestingHeightmapParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
