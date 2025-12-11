package com.ignfab.minalac.generator.parameters.tasks;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.cfg.MapperBuilder;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.heightmaps.TestingHeightmapParams;
import com.ignfab.minalac.generator.parameters.heightmaps.WritableHeightmapParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingPlaceableParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class RenderHeightmapTaskParamsTest {
    @Test
    public void testDeserializeAt() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("ground", 5));

        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("heightmapRenderer", RenderHeightmapTaskParams.class);

        RenderHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            RenderHeightmapTaskParams.class,
            """
            type: heightmapRenderer
            at: ground
            place: somethingInvisible
            """,
            builder
        ));
        assertInstanceOf(WritableHeightmapParams.class, params.at);
        assertEquals("somethingInvisible",  assertInstanceOf(TestingVoxelParams.class, params.place).name);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    public void testDeserializeMinMax() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        generation.heightmaps().add(new HeightmapDeclaration("water", 5));
        generation.heightmaps().add(new HeightmapDeclaration("ground", 25));

        MapperBuilder<?, ?> builder = ParamsTester.mapperBuilderWithParams("heightmapRenderer", RenderHeightmapTaskParams.class);

        RenderHeightmapTaskParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            RenderHeightmapTaskParams.class,
            """
            type: heightmapRenderer
            minimum: water
            maximum: ground
            place: somethingInvisible
            """,
            builder
        ));
        assertInstanceOf(WritableHeightmapParams.class, params.minimum);
        assertInstanceOf(WritableHeightmapParams.class, params.maximum);
        assertEquals("somethingInvisible",  assertInstanceOf(TestingVoxelParams.class, params.place).name);

        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    public void testValidateCorrectFieldsCombination() {
        // Invalid combination

        RenderHeightmapTaskParams paramsWithAtMinMax = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithAtMinMax.at = TestingHeightmapParams.VALID;
        paramsWithAtMinMax.minimum = TestingHeightmapParams.VALID;
        paramsWithAtMinMax.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMinMax::validate);

        RenderHeightmapTaskParams paramsWithAtMax = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithAtMax.at = TestingHeightmapParams.VALID;
        paramsWithAtMax.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMax::validate);

        RenderHeightmapTaskParams paramsWithAtMin = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithAtMin.at = TestingHeightmapParams.VALID;
        paramsWithAtMin.minimum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithAtMin::validate);

        RenderHeightmapTaskParams paramsWithMinOnly = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithMinOnly.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithMinOnly::validate);

        RenderHeightmapTaskParams paramsWithMaxOnly = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithMaxOnly.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsWithMaxOnly::validate);

        // Valid combination

        RenderHeightmapTaskParams paramsWithAt = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithAt.minimum = TestingHeightmapParams.VALID;
        paramsWithAt.maximum = TestingHeightmapParams.VALID;
        assertDoesNotThrow(paramsWithAt::validate);

        RenderHeightmapTaskParams paramsWithMinMax = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsWithMinMax.at = TestingHeightmapParams.VALID;
        assertDoesNotThrow(paramsWithMinMax::validate);
    }

    @Test
    public void testValidateAt() {
        RenderHeightmapTaskParams paramsAtInvalid = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsAtInvalid.at = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, paramsAtInvalid::validate);

        RenderHeightmapTaskParams paramsPlaceInvalid = new RenderHeightmapTaskParams(TestingPlaceableParams.INVALID);
        paramsPlaceInvalid.at = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsPlaceInvalid::validate);
    }

    @Test
    public void testValidateMinMax() {
        RenderHeightmapTaskParams paramsMinInvalid = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsMinInvalid.minimum = TestingHeightmapParams.INVALID;
        paramsMinInvalid.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsMinInvalid::validate);

        RenderHeightmapTaskParams paramsMaxInvalid = new RenderHeightmapTaskParams(TestingPlaceableParams.VALID);
        paramsMaxInvalid.minimum = TestingHeightmapParams.VALID;
        paramsMaxInvalid.maximum = TestingHeightmapParams.INVALID;
        assertThrows(IllegalArgumentException.class, paramsMaxInvalid::validate);

        RenderHeightmapTaskParams paramsPlaceInvalid = new RenderHeightmapTaskParams(TestingPlaceableParams.INVALID);
        paramsPlaceInvalid.minimum = TestingHeightmapParams.VALID;
        paramsPlaceInvalid.maximum = TestingHeightmapParams.VALID;
        assertThrows(IllegalArgumentException.class, paramsPlaceInvalid::validate);
    }
}
