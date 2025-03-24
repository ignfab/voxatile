package com.ignfab.minalac.generator.parameters.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;


import static org.junit.jupiter.api.Assertions.*;

public class VectorRendererParamsTest {
    @Test
    public void testValidate() {
        ModelSelectionParams selection = new ModelSelectionParams("building");
        VectorRendererParams params;

        // Test required arguments
        params = new VectorRendererParams(new ModelSelectionParams(""), TestingHeightmapParams.VALID);
        params.place = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.INVALID);
        params.place = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = new TestingVoxelTypeParams("A");
        assertDoesNotThrow(params::validate);

        // Test optional arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = new TestingVoxelTypeParams("A");
        params.inside = new TestingVoxelTypeParams("B");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.inside = new TestingVoxelTypeParams("B");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = new TestingVoxelTypeParams("A");
        assertDoesNotThrow(params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = new TestingVoxelTypeParams("C");
        assertDoesNotThrow(params::validate);

        // Test incompatible arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = new TestingVoxelTypeParams("C");
        params.borders = new TestingVoxelTypeParams("A");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = new TestingVoxelTypeParams("C");
        params.inside = new TestingVoxelTypeParams("B");
        assertThrows(IllegalArgumentException.class, params::validate);

        // Test invalid arguments
        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.borders = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.inside = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

        params = new VectorRendererParams(selection, TestingHeightmapParams.VALID);
        params.place = new TestingVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);

    }
}
