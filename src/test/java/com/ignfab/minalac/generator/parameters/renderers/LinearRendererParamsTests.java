package com.ignfab.minalac.generator.parameters.renderers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.common.IntegerIntervalsParams;
import com.ignfab.minalac.generator.parameters.placeables.TestingVoxelTypeParams;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class LinearRendererParamsTests {
    @Test
    void testDeserializeMinimal() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0);
        LinearRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(LinearRendererParams.class,
            """
                type: LinearRendererParams
                modelType: toto
                place: titi
            """));

        assertEquals("toto", params.modelType);
        assertEquals("titi", assertInstanceOf(TestingVoxelTypeParams.class, params.place).name);
        assertNull(params.renderOnlyWhenAbove);
        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    void testDeserializeFull() {
        Generation generation = new Generation(new TestingVoxelWorld(new WorldBBox3d(0, 0, 0, 1, 1, 1)), null, null, 0, 0, 1, 1, 1.0, 1.0);
        generation.heightmaps().add("ground", new Heightmap(0, 0, 1, 1, 0));

        LinearRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(LinearRendererParams.class,
            """
                type: LinearRendererParams
                modelType: toto
                place: titi
                at: 1..3
                renderOnlyWhenAbove: ground
            """));

        assertEquals("toto", params.modelType);
        assertEquals("titi", assertInstanceOf(TestingVoxelTypeParams.class, params.place).name);
        assertEquals(params.renderOnlyWhenAbove, "ground");
        assertInstanceOf(IntegerIntervalsParams.class, params.at);
        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(() -> params.create(generation));
    }

    @Test
    void testInvalidAt() {
        LinearRendererParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(LinearRendererParams.class,
            """
                type: LinearRendererParams
                modelType: toto
                place: titi
                at: 3..1
            """));

        assertThrows(IllegalArgumentException.class, () -> params.validate());
    }

}
