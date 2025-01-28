package com.ignfab.minalac.generator.parameters.renderers;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MTVoxelTypeParams;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorRendererParamsTest {
    @Test
    public void testValidate() {

        MTVoxelTypeParams grass = new MTVoxelTypeParams("default:grass");
        MTVoxelTypeParams brick = new MTVoxelTypeParams("default:stonebrick");
        ModelSelectionParams selection = new ModelSelectionParams("building");

        VectorRendererParams paramsWithoutType = new VectorRendererParams(new ModelSelectionParams(""), "ground", grass, brick);
        assertThrows(IllegalArgumentException.class, paramsWithoutType::validate);

        VectorRendererParams paramsWithoutHeightmap = new VectorRendererParams(selection, "", grass, brick);
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        VectorRendererParams params = new VectorRendererParams(selection, "ground", grass, brick);
        assertDoesNotThrow(params::validate);
    }
}
