package com.ignfab.minalac.generator.parameters.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.ElasticPlaceableStructureParams;
import com.ignfab.minalac.generator.parameters.placeables.structures.ElasticPlaceableStructureParamsTest;

import static org.junit.jupiter.api.Assertions.*;

public class RenderFacadesTaskParamsTest {

    @Test
    public void testValidate() {
        ModelSelectionParams invalidModelSelection = new ModelSelectionParams("");
        ModelSelectionParams validModelSelection = new ModelSelectionParams("valid");

        assertDoesNotThrow(
            new RenderFacadesTaskParams(
                validModelSelection,
                TestingHeightmapParams.VALID,
                "height",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID)
            )::validate);

        assertThrows(
            IllegalArgumentException.class,
            new RenderFacadesTaskParams(
                validModelSelection,
                TestingHeightmapParams.VALID,
                "",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID)
            )::validate);

        assertThrows(
            IllegalArgumentException.class,
            new RenderFacadesTaskParams(
                invalidModelSelection,
                TestingHeightmapParams.VALID,
                "height",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID)
            )::validate);

        assertThrows(
            IllegalArgumentException.class,
            new RenderFacadesTaskParams(
                validModelSelection,
                TestingHeightmapParams.INVALID,
                "height",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID)
            )::validate);

        assertThrows(
            IllegalArgumentException.class,
            new RenderFacadesTaskParams(
                validModelSelection,
                TestingHeightmapParams.VALID,
                "height",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.INVALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID)
            )::validate);

        assertThrows(
            IllegalArgumentException.class,
            new RenderFacadesTaskParams(
                validModelSelection,
                TestingHeightmapParams.VALID,
                "height",
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.VALID),
                new ElasticPlaceableStructureParams(ElasticPlaceableStructureParamsTest.TestingStructure.INVALID)
            )::validate);
    }

    @Test
    public void testDeserialize() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(RenderFacadesTaskParams.class, "renderFacades"));

        RenderFacadesTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            RenderFacadesTaskParams.class,
            """
            type: renderFacades
            models:
              type: buildings
            heightmap: ground
            heightName: height
            ground:
              structure:
                - put: some-voxel
                  at: [1, 2, 3]
            floor:
              structure:
                - put: some-voxel
                  at: [5, 2, 3]
                - put: some-voxel
                  at: [0, 0, 0]
              elasticAtX: 1
            """,
            mapper
        ));
        assertInstanceOf(ModelSelectionParams.class, params.models);
        assertInstanceOf(ReadableHeightmapParams.class, params.heightmap);
        assertInstanceOf(ElasticPlaceableStructureParams.class, params.ground);
        assertInstanceOf(ElasticPlaceableStructureParams.class, params.floor);
    }
}
