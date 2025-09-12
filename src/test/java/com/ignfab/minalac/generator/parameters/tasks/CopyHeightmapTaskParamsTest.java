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
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CopyHeightmapTaskParamsTest {
    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("ground", 5));
        generation.heightmaps().add(new HeightmapDeclaration("water", 1));

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
        assertInstanceOf(WritableHeightmapParams.class, params.from);
        assertEquals("ground", params.to.stored);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));

        assertDoesNotThrow(
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
        ModelSelectionParams selection = new ModelSelectionParams();
        selection.type = "Ok";

        CopyHeightmapTaskParams paramsWithNullModels = new CopyHeightmapTaskParams(
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.VALID
        );

        assertThrows(IllegalArgumentException.class, paramsWithNullModels::validate);

        CopyHeightmapTaskParams paramsWithInvalidFrom = new CopyHeightmapTaskParams(
            TestingHeightmapParams.INVALID,
            TestingHeightmapParams.VALID
        );

        paramsWithInvalidFrom.models = selection;

        assertThrows(IllegalArgumentException.class, paramsWithInvalidFrom::validate);

        CopyHeightmapTaskParams paramsWithInvalidTo = new CopyHeightmapTaskParams(
            TestingHeightmapParams.VALID,
            TestingHeightmapParams.INVALID
        );

        paramsWithInvalidTo.models = selection;

        assertThrows(IllegalArgumentException.class, paramsWithInvalidTo::validate);
    }
}
