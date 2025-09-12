package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class RenderBuildingsTaskParamsTest {
    @Test
    public void testValidate() {
        TestingVoxelParams placeable = new TestingVoxelParams("voxel");

        RenderBuildingsTaskParams paramsWithoutModels = new RenderBuildingsTaskParams(TestingHeightmapParams.VALID, placeable, placeable, placeable);
        assertThrows(IllegalArgumentException.class, paramsWithoutModels::validate);

        RenderBuildingsTaskParams paramsWithoutHeightmap = new RenderBuildingsTaskParams(TestingHeightmapParams.INVALID, placeable, placeable, placeable);
        paramsWithoutHeightmap.models = new ModelSelectionParams("building");
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        RenderBuildingsTaskParams params = new RenderBuildingsTaskParams(TestingHeightmapParams.VALID, placeable, placeable, placeable);
        params.models = new ModelSelectionParams("building");
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(RenderBuildingsTaskParams.class, "building"));

        RenderBuildingsTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof: voxelA
        wall: voxelB
        window: voxelC
        """, mapper));
        assertEquals("building", params.models.type);
        assertEquals("ground", assertInstanceOf(WritableHeightmapParams.class, params.heightmap).stored);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelParams.class, params.roof).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelParams.class, params.wall).name);
        assertEquals("voxelC", assertInstanceOf(TestingVoxelParams.class, params.window).name);

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        heightmap:
        roof: voxelA
        wall: voxelB
        window: voxelC
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof:
        wall: voxelB
        window: voxelC
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof: voxelA
        wall:
        window: voxelC
        """, mapper));


        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(RenderBuildingsTaskParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof: voxelA
        wall: voxelB
        window:
        """, mapper));
    }
}
