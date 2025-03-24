package com.ignfab.minalac.generator.parameters.renderers;

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
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelTypeParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HeightmapRendererParamsTest {
    @Test
    public void testDeserializeAt() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 5));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(HeightmapRendererParams.class, "heightmapRenderer"));

        HeightmapRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            HeightmapRendererParams.class,
            """
            type: heightmapRenderer
            at: ground
            place: somethingInvisible
            """,
            mapper
        ));
        assertInstanceOf(StoredHeightmapParams.class, params.at);
        assertEquals("somethingInvisible",  assertInstanceOf(TestingVoxelTypeParams.class, params.place).name);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    public void testDeserializeMinMax() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0, 0.0);
        generation.heightmaps().add("water", new Heightmap(0, 0, 1, 1, 5));
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 25));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(HeightmapRendererParams.class, "heightmapRenderer"));

        HeightmapRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            HeightmapRendererParams.class,
            """
            type: heightmapRenderer
            minimum: water
            maximum: ground
            place: somethingInvisible
            """,
            mapper
        ));
        assertInstanceOf(StoredHeightmapParams.class, params.minimum);
        assertInstanceOf(StoredHeightmapParams.class, params.maximum);
        assertEquals("somethingInvisible",  assertInstanceOf(TestingVoxelTypeParams.class, params.place).name);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    public void testValidateCorrectFieldsCombination() {
        // Invalid combination

        HeightmapRendererParams paramsWithAtMinMax = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithAtMinMax.at = TestingHeightmapParams.VALID;
        paramsWithAtMinMax.minimum = TestingHeightmapParams.VALID;
        paramsWithAtMinMax.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMinMax::validate);

        HeightmapRendererParams paramsWithAtMax = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithAtMax.at = TestingHeightmapParams.VALID;
        paramsWithAtMax.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMax::validate);

        HeightmapRendererParams paramsWithAtMin = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithAtMin.at = TestingHeightmapParams.VALID;
        paramsWithAtMin.minimum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMin::validate);

        HeightmapRendererParams paramsWithMinOnly = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithMinOnly.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithMinOnly::validate);

        HeightmapRendererParams paramsWithMaxOnly = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithMaxOnly.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithMaxOnly::validate);

        // Valid combination

        HeightmapRendererParams paramsWithAt = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithAt.minimum = TestingHeightmapParams.VALID;
        paramsWithAt.maximum = TestingHeightmapParams.VALID;
        assertDoesNotThrow(paramsWithAt::validate);

        HeightmapRendererParams paramsWithMinMax = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsWithMinMax.at = TestingHeightmapParams.VALID;
        assertDoesNotThrow(paramsWithMinMax::validate);
    }

    @Test
    public void testValidateAt() {
        HeightmapRendererParams paramsAtInvalid = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsAtInvalid.at = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, paramsAtInvalid::validate);

        HeightmapRendererParams paramsPlaceInvalid = new HeightmapRendererParams(TestingPlaceableParams.INVALID);
        paramsPlaceInvalid.at = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsPlaceInvalid::validate);
    }

    @Test
    public void testValidateMinMax() {
        HeightmapRendererParams paramsMinInvalid = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsMinInvalid.minimum = TestingHeightmapParams.INVALID;
        paramsMinInvalid.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsMinInvalid::validate);

        HeightmapRendererParams paramsMaxInvalid = new HeightmapRendererParams(TestingPlaceableParams.VALID);
        paramsMaxInvalid.minimum = TestingHeightmapParams.VALID;
        paramsMaxInvalid.maximum = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, paramsMaxInvalid::validate);

        HeightmapRendererParams paramsPlaceInvalid = new HeightmapRendererParams(TestingPlaceableParams.INVALID);
        paramsPlaceInvalid.minimum = TestingHeightmapParams.VALID;
        paramsPlaceInvalid.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsPlaceInvalid::validate);
    }
}
