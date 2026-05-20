package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.TestingPlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLinesTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLinesTaskParams params;

        // Test required arguments
        params = new RenderLinesTaskParams(TestingPlaceableStructureParams.VALID);
        params.models = TestingModelSelectionParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLinesTaskParams(TestingPlaceableStructureParams.INVALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLinesTaskParams(TestingPlaceableStructureParams.VALID);
        assertDoesNotThrow(params::validate);
    }
}
