package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.exc.InvalidNullException;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderBuildingsTaskParamsTest {
    @Test
    public void testValidate() {
        RenderBuildingsTaskParams params;
        TestingVoxelParams placeable = new TestingVoxelParams("voxel");

        params = new RenderBuildingsTaskParams(TestingModelSelectionParams.VALID, placeable, placeable, placeable);
        assertDoesNotThrow(params::validate);

        params = new RenderBuildingsTaskParams(TestingModelSelectionParams.INVALID, placeable, placeable, placeable);
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testDeserialization() {
        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("building", RenderBuildingsTaskParams.class);

        RenderBuildingsTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        roof: voxelA
        wall: voxelB
        window: voxelC
        """, builder));
        assertEquals("building", params.models.type);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelParams.class, params.roof).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelParams.class, params.wall).name);
        assertEquals("voxelC", assertInstanceOf(TestingVoxelParams.class, params.window).name);

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        roof:
        wall: voxelB
        window: voxelC
        """, builder));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        roof: voxelA
        wall:
        window: voxelC
        """, builder));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        roof: voxelA
        wall: voxelB
        window:
        """, builder));
    }
}
