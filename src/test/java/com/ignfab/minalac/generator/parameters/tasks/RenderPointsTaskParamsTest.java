package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderPointsTaskParamsTest {

    @Test
    public void testValidate() {
        RenderPointsTaskParams params;

        // Check any invalid member makes validation throw
        params = new RenderPointsTaskParams(TestingPlaceableStructureParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderPointsTaskParams(TestingPlaceableStructureParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        // All valid
        params = new RenderPointsTaskParams(TestingPlaceableStructureParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
