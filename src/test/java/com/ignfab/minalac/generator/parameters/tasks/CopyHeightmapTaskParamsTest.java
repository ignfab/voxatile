package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.StoredHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.models.TestingModelSelectionParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CopyHeightmapTaskParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 5));
        generation.heightmaps().add("water", new Heightmap(0, 0, 1, 1, 1));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(CopyHeightmapTaskParams.class, "copyHeightmap"));

        CopyHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            CopyHeightmapTaskParams.class,
            """
            type: copyHeightmap
            models:
              type: water
            from: water
            to: ground
            """,
            mapper
        ));
        assertInstanceOf(ModelSelectionParams.class, params.models);
        assertInstanceOf(StoredHeightmapParams.class, params.from);
        assertEquals("ground", params.to.stored);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                from: water
                to: ground
                """,
            mapper
        ));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                models:
                  type: water
                to: ground
                """,
                mapper
            ));

        assertThrows(
            JacksonException.class,
            () -> ParamsTester.deserialize(
                CopyHeightmapTaskParams.class,
                """
                type: copyHeightmap
                models:
                  type: water
                from: water
                """,
                mapper
            ));
    }

    @Test
    public void testValidate() {
        CopyHeightmapTaskParams paramsWithInvalidModels = new CopyHeightmapTaskParams(
            TestingModelSelectionParams.INVALID,
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidModels::validate);

        CopyHeightmapTaskParams paramsWithInvalidFrom = new CopyHeightmapTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.INVALID,
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidFrom::validate);

        CopyHeightmapTaskParams paramsWithInvalidTo = new CopyHeightmapTaskParams(
            TestingModelSelectionParams.VALID,
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.INVALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidTo::validate);
    }
}
