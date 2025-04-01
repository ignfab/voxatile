package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.StoredHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingRendererParamsTest {
    @Test
    public void testValidate() {
        TestingVoxelTypeParams placeable = new TestingVoxelTypeParams("voxel");
        BuildingRendererParams paramsWithoutType = new BuildingRendererParams(new ModelSelectionParams(""), TestingHeightmapParams.VALID, placeable, placeable, placeable);
        assertThrows(IllegalArgumentException.class, paramsWithoutType::validate);

        BuildingRendererParams paramsWithoutHeightmap = new BuildingRendererParams(new ModelSelectionParams("building"), TestingHeightmapParams.INVALID, placeable, placeable, placeable);
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        BuildingRendererParams params = new BuildingRendererParams(new ModelSelectionParams("building"), TestingHeightmapParams.VALID, placeable, placeable, placeable);
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(BuildingRendererParams.class, "building"));

        BuildingRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(BuildingRendererParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof: voxelA
        wall: voxelB
        window: voxelC
        """, mapper));
        assertEquals("building", params.models.type);
        assertEquals("ground", assertInstanceOf(StoredHeightmapParams.class, params.heightmap).stored);
        assertEquals("voxelA", assertInstanceOf(TestingVoxelTypeParams.class, params.roof).name);
        assertEquals("voxelB", assertInstanceOf(TestingVoxelTypeParams.class, params.wall).name);
        assertEquals("voxelC", assertInstanceOf(TestingVoxelTypeParams.class, params.window).name);

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(BuildingRendererParams.class, """
        type: building
        models:
          type: building
        heightmap:
        roof: voxelA
        wall: voxelB
        window: voxelC
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(BuildingRendererParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof:
        wall: voxelB
        window: voxelC
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(BuildingRendererParams.class, """
        type: building
        models:
          type: building
        heightmap: ground
        roof: voxelA
        wall:
        window: voxelC
        """, mapper));


        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(BuildingRendererParams.class, """
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
