package com.ignfab.minalac.generator.parameters.heightmaps;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JacksonException;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.utils.IntegerIntervalParams;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemapHeightmapParamsTest {

    @Test
    public void testDeserialize() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        HeightmapDeclaration heightmapSpec = (new HeightmapDeclaration("lotad", 0));
        generation.heightmaps().add(heightmapSpec);

        GenerationTile tile = new GenerationTile(generation, new WorldBBox3d(-3, 0, 0, 8, 1, 0));
        WritableHeightmap base = tile.heightmaps().get(heightmapSpec.spec());

        for (int i = -3; i <= 4; i++)
            base.set(i, 0, i);

        RemapHeightmapParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            RemapHeightmapParams.class,
            """
            remap: lotad
            mapping:
              0..1: 21
              -3..3: 34
            """
        ));

        ReadableHeightmapSpec spec = assertDoesNotThrow(() -> params.create(generation.heightmaps()));
        ReadableHeightmap result = tile.heightmaps().get(spec);
        assertEquals(21, result.get(0, 0), "Should match first interval [0; 1] -> 21");
        assertEquals(34, result.get(2, 0), "Should match second interval [-3; 3] -> 34");
        assertEquals(34, result.get(-3, 0), "Should match second interval [-3; 3] -> 34");
        assertEquals(4, result.get(4, 0), "Doesn't any interval should return base value -> 4");

        assertThrows(JacksonException.class, () -> ParamsTester.deserialize(
            RemapHeightmapParams.class,
            """
            remap: base
            mapping:
              252..386: 3
              notInterval: 10
            """
        ));
    }

    @Test
    public void testValidate() {
        // Testing base validation is propagated.
        LinkedHashMap<IntegerIntervalParams, Integer> validMap = new LinkedHashMap<>();
        validMap.put(new IntegerIntervalParams.FallbackParams(2, 5), 7);

        RemapHeightmapParams paramsBaseInvalid = new RemapHeightmapParams(TestingHeightmapParams.INVALID, validMap);
        assertThrows(IllegalArgumentException.class, paramsBaseInvalid::validate);

        // Testing intervals validation is propagated.
        LinkedHashMap<IntegerIntervalParams, Integer> invalidMap = new LinkedHashMap<>();
        invalidMap.put(new IntegerIntervalParams.FallbackParams(2, 5), 7);
        invalidMap.put(new IntegerIntervalParams.FallbackParams(7, 5), 8);

        RemapHeightmapParams paramsMapInvalid = new RemapHeightmapParams(TestingHeightmapParams.VALID, invalidMap);
        assertThrows(IllegalArgumentException.class, paramsMapInvalid::validate);
    }
}
