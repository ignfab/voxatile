package com.ignfab.minalac.generator.parameters.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;


import static org.junit.jupiter.api.Assertions.*;

public class VectorRendererParamsTest {
    @Test
    public void testValidate() {
        ModelSelectionParams selection = new ModelSelectionParams("building");
        VectorRendererParams params;

        // Test required arguments
        params = new VectorRendererParams(new ModelSelectionParams(""), TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.INVALID);
        params.place = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        // Test optional arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        params.inside = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.inside = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        assertDoesNotThrow(params::validate);

        // Test incompatible arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        params.borders = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.VALID;
        params.inside = TestingPlaceableParams.VALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        // Test invalid arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.inside = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = TestingPlaceableParams.INVALID;
        assertThrows(IllegalArgumentException.class, params::validate);

    }
}
