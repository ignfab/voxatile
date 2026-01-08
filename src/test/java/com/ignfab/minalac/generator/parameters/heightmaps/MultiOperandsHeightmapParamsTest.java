package com.ignfab.minalac.generator.parameters.heightmaps;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultiOperandsHeightmapParamsTest {
    @Test
    public void testDeserializeSum() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        HeightmapDeclaration heightmapSpec = (new HeightmapDeclaration("ground", 0));
        generation.heightmaps().add(heightmapSpec);

        GenerationTile tile = new GenerationTile(generation, new WorldBBox3d(0, 0, 0, 3, 1, 1));
        WritableHeightmap ground = tile.heightmaps().get(heightmapSpec.spec());

        ground.set(0, 0, 0);
        ground.set(1, 0, 2);
        ground.set(2, 0, 3);

        MultiOperandsHeightmapParams.Sum params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            MultiOperandsHeightmapParams.Sum.class,
            """
            sum:
              - ground
              - 5
              - 6
            """));

        assertDoesNotThrow(params::validate);
        ReadableHeightmapSpec spec = assertDoesNotThrow(() -> params.create(generation.heightmaps()));
        ReadableHeightmap result = tile.heightmaps().get(spec);

        assertEquals(11, result.get(0, 0), "0 + 5 + 6");
        assertEquals(13, result.get(1, 0), "2 + 5 + 6");
        assertEquals(14, result.get(2, 0), "3 + 5 + 6");
    }

    @Test
    public void testDeserializeProduct() {
        Generation generation = new Generation(new TestingVoxelWorld(), TestingSeed.UNUSED, null, 0, 0, 1, 1, 1.0, 1.0, 0.0, 100);
        HeightmapDeclaration heightmapSpec = (new HeightmapDeclaration("ground", 0));
        generation.heightmaps().add(heightmapSpec);

        GenerationTile tile = new GenerationTile(generation, new WorldBBox3d(0, 0, 0, 3, 1, 1));
        WritableHeightmap ground = tile.heightmaps().get(heightmapSpec.spec());

        ground.set(0, 0, 0);
        ground.set(1, 0, 2);
        ground.set(2, 0, 3);

        MultiOperandsHeightmapParams.Product params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            MultiOperandsHeightmapParams.Product.class,
            """
            product:
              - ground
              - 5
              - 7
            """));

        assertDoesNotThrow(params::validate);
        ReadableHeightmapSpec spec = assertDoesNotThrow(() -> params.create(generation.heightmaps()));
        ReadableHeightmap result = tile.heightmaps().get(spec);

        assertEquals(0, result.get(0, 0), "0 * 5 * 7");
        assertEquals(70, result.get(1, 0), "2 * 5 * 7");
        assertEquals(105, result.get(2, 0), "3 * 5 * 7");
    }

    @Test
    public void testValidate() {
        // Sum and Product use the same validate()
        assertThrows(IllegalArgumentException.class, (new MultiOperandsHeightmapParams.Sum(Collections.emptyList()))::validate);

        // Testing first validation is propagated.
        MultiOperandsHeightmapParams.Sum paramsFirstInvalid = new MultiOperandsHeightmapParams.Sum(
            List.of(
                TestingHeightmapParams.INVALID,
                TestingHeightmapParams.VALID
            )
        );
        assertThrows(IllegalArgumentException.class, paramsFirstInvalid::validate);

        // Testing second validation is propagated.
        MultiOperandsHeightmapParams.Product paramsSecondInvalid = new MultiOperandsHeightmapParams.Product(
            List.of(
                TestingHeightmapParams.VALID,
                TestingHeightmapParams.INVALID
            )
        );
        assertThrows(IllegalArgumentException.class, paramsSecondInvalid::validate);
    }
}
