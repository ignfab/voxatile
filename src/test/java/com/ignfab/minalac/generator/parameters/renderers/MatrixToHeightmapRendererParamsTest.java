package com.ignfab.minalac.generator.parameters.renderers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixToHeightmapRendererParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 5));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MatrixToHeightmapRendererParams.class, "matrixToHeightmap"));

        MatrixToHeightmapRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            MatrixToHeightmapRendererParams.class,
            """
            type: matrixToHeightmap
            models:
              type: mnt
            heightmap: ground
            """,
            mapper
        ));
        assertInstanceOf(ModelSelectionParams.class, params.models);
        assertEquals("ground", params.heightmap.name);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                MatrixToHeightmapRendererParams.class,
                """
                type: matrixToHeightmap
                heightmap: ground
                """,
                mapper
            ));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                MatrixToHeightmapRendererParams.class,
                """
                type: matrixToHeightmap
                models:
                  type: mnt
                """,
                mapper
            ));

    }

    @Test
    public void testValidate() {
        MatrixToHeightmapRendererParams paramsWithInvalidModels = new MatrixToHeightmapRendererParams(
            // Invalid ModelSelectionParams
            new ModelSelectionParams(""),
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidModels::validate);

        MatrixToHeightmapRendererParams paramsWithInvalidHeightmap = new MatrixToHeightmapRendererParams(
            new ModelSelectionParams("again"),
            TestingHeightmapParams.INVALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidHeightmap::validate);
    }
}
