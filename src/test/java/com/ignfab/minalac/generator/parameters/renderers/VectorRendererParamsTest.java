package com.ignfab.minalac.generator.parameters.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;


import static org.junit.jupiter.api.Assertions.*;

public class VectorRendererParamsTest {
    @Test
    public void testValidate() {
        ModelSelectionParams selection = new ModelSelectionParams("building");
        VectorRendererParams params;

        // Test required arguments
        params = new VectorRendererParams(new ModelSelectionParams(""), "ground");
        params.place = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "");
        params.place = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "ground");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.borders = new TestingVoxelTypeParams("A");
        assertDoesNotThrow(params::validate);

        // Test optional arguments
        params = new VectorRendererParams(selection, "ground");
        params.borders = new TestingVoxelTypeParams("A");
        params.inside = new TestingVoxelTypeParams("B");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.inside = new TestingVoxelTypeParams("B");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.borders = new TestingVoxelTypeParams("A");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.place = new TestingVoxelTypeParams("C");
        assertDoesNotThrow(params::validate);

        // Test incompatible arguments
        params = new VectorRendererParams(selection, "ground");
        params.place = new TestingVoxelTypeParams("C");
        params.borders = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.place = new TestingVoxelTypeParams("C");
        params.inside = new TestingVoxelTypeParams("B");
        assertThrows(IllegalArgumentException.class, params::validate);

        // Test invalid arguments
        params = new VectorRendererParams(selection, "ground");
        params.borders = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.inside = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, "ground");
        params.place = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

    }
}
