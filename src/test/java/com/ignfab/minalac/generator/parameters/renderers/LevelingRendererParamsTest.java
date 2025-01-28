package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LevelingRendererParamsTest {
    @Test
    public void testValidate() {
        TestingVoxelTypeParams placeable = new TestingVoxelTypeParams("voxel");

        LevelingRendererParams paramsWithoutType = new LevelingRendererParams(new ModelSelectionParams(""), "ground", placeable);
        assertThrows(IllegalArgumentException.class, paramsWithoutType::validate);

        LevelingRendererParams paramsWithoutHeightmap = new LevelingRendererParams(new ModelSelectionParams("building"), "", placeable);
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        LevelingRendererParams params = new LevelingRendererParams(new ModelSelectionParams("building"), "ground", placeable);
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(LevelingRendererParams.class, "leveling"));

        LevelingRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(LevelingRendererParams.class, """
        type: leveling
        models:
          type: building
        heightmap: ground
        filling: voxel
        """, mapper));
        assertEquals("building", params.models.type);
        assertEquals("ground", params.heightmap);
        assertEquals("voxel", assertInstanceOf(TestingVoxelTypeParams.class,  params.filling).name);

        assertThrows(MismatchedInputException.class, () -> ParamsTester.deserialize(LevelingRendererParams.class, """
        type: leveling
        models:
          type: building
        heightmap:
        filling: voxel
        """, mapper));

        assertThrows(InvalidNullException.class, () -> ParamsTester.deserialize(LevelingRendererParams.class, """
        type: leveling
        models:
          type: building
        heightmap: ground
        filling:
        """, mapper));
    }
}
