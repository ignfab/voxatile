package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.TestingGeneration;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;

import static org.junit.jupiter.api.Assertions.*;

public class PopulateHeightmapTaskParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new TestingGeneration();
        generation.heightmaps().add(new HeightmapDeclaration("ground", 5));

        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("matrixToHeightmap", PopulateHeightmapTaskParams.class);

        PopulateHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            PopulateHeightmapTaskParams.class,
            """
            type: matrixToHeightmap
            models:
              type: mnt
            heightmap: ground
            """,
            builder
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
                models:
                  type: mnt
                """,
                builder
            )
        );
    }

    @Test
    public void testValidate() {
        PopulateHeightmapTaskParams paramsWithInvalidHeightmap = new PopulateHeightmapTaskParams(
            TestingHeightmapParams.INVALID
        );
        paramsWithInvalidHeightmap.models.type = "Ok";

        assertThrows(IllegalArgumentException.class, paramsWithInvalidHeightmap::validate);
    }
}
