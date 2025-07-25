package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;

import static org.junit.jupiter.api.Assertions.*;

public class LevelHeightmapTaskParamsTest {
    @Test
    public void testValidate() {
        LevelGroundTaskParams paramsWithoutType = new LevelGroundTaskParams(TestingModelSelectionParams.INVALID, TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        assertThrows(IllegalArgumentException.class, paramsWithoutType::validate);

        LevelGroundTaskParams paramsWithoutHeightmap = new LevelGroundTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.INVALID, TestingPlaceableParams.VALID);
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        LevelGroundTaskParams params = new LevelGroundTaskParams(TestingModelSelectionParams.VALID, TestingHeightmapParams.VALID, TestingPlaceableParams.VALID);
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(LevelGroundTaskParams.class, "leveling"));

        LevelGroundTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(LevelGroundTaskParams.class, """
        type: leveling
        models:
          type: building
        heightmap: ground
        filling: voxel
        """, mapper));
        assertEquals("building", params.models.type);
        assertEquals("ground", params.heightmap.stored);
        assertEquals("voxel", assertInstanceOf(TestingVoxelParams.class,  params.filling).name);

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(LevelGroundTaskParams.class, """
        type: leveling
        models:
          type: building
        heightmap:
        filling: voxel
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(LevelGroundTaskParams.class, """
        type: leveling
        models:
          type: building
        heightmap: ground
        filling:
        """, mapper));
    }
}
