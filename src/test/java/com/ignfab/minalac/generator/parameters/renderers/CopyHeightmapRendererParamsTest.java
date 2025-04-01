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
import com.ignfab.minalac.generator.parameters.heightmaps.StoredHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CopyHeightmapRendererParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 5));
        generation.heightmaps().add("water", new Heightmap(0, 0, 1, 1, 1));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(CopyHeightmapRendererParams.class, "copyHeightmap"));

        CopyHeightmapRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            CopyHeightmapRendererParams.class,
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
                CopyHeightmapRendererParams.class,
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
                CopyHeightmapRendererParams.class,
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
                CopyHeightmapRendererParams.class,
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
        CopyHeightmapRendererParams paramsWithInvalidModels = new CopyHeightmapRendererParams(
            // Invalid ModelSelectionParams
            new ModelSelectionParams(""),
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidModels::validate);

        CopyHeightmapRendererParams paramsWithInvalidFrom = new CopyHeightmapRendererParams(
            new ModelSelectionParams("4"),
            TestingHeightmapParams.INVALID,
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidFrom::validate);

        CopyHeightmapRendererParams paramsWithInvalidTo = new CopyHeightmapRendererParams(
            new ModelSelectionParams("2"),
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.INVALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithInvalidTo::validate);
    }
}
