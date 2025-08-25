package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PopulateHeightmapTaskParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("ground", 5));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(PopulateHeightmapTaskParams.class, "matrixToHeightmap"));

        PopulateHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            PopulateHeightmapTaskParams.class,
            """
            type: matrixToHeightmap
            models:
              type: mnt
            heightmap: ground
            """,
            mapper
        ));
        assertInstanceOf(ModelSelectionParams.class, params.models);
        assertEquals("ground", params.heightmap.stored);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                PopulateHeightmapTaskParams.class,
                """
                type: matrixToHeightmap
                heightmap: ground
                """,
                mapper
            ));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                PopulateHeightmapTaskParams.class,
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
        PopulateHeightmapTaskParams paramsWithInvalidModels = new PopulateHeightmapTaskParams(
            // Invalid ModelSelectionParams
            new ModelSelectionParams(""),
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidModels::validate);

        PopulateHeightmapTaskParams paramsWithInvalidHeightmap = new PopulateHeightmapTaskParams(
            new ModelSelectionParams("again"),
            TestingHeightmapParams.INVALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidHeightmap::validate);
    }
}
