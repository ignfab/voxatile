package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderLinesTaskParamsTest {

    @Test
    public void testValidate() throws JsonProcessingException {
        RenderLinesTaskParams params;

        // Prepare invalid and valid structure params
        PlaceableStructureParams validStruct = ParamsTester.deserialize(PlaceableStructureParams.class, "{ \"at\": [0, 0, 0], \"put\": 'A' }");
        assertDoesNotThrow(validStruct::validate);
        PlaceableStructureParams invalidStruct = ParamsTester.deserialize(PlaceableStructureParams.class, "{ \"axes\": [], \"with\": {}, \"blueprint\": 'A' }");
        assertThrows(IllegalArgumentException.class, invalidStruct::validate);

        // Test required arguments
        params = new RenderLinesTaskParams(validStruct);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLinesTaskParams(invalidStruct);
        params.models = TestingModelSelectionParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new RenderLinesTaskParams(validStruct);
        params.models = TestingModelSelectionParams.VALID;
        assertDoesNotThrow(params::validate);
    }
}
