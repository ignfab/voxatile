package com.ignfab.minalac.generator.generation.heighmaps;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.SimpleBinaryOperatorHeightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleBinaryOperatorHeightmapTest {
    @Test
    public void testGet() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 4);
        Heightmap first = new Heightmap(bbox, 0);
        Heightmap second = new Heightmap(bbox, 1);

        first.set(-1, -2, 1);
        first.set(-1, -1, 2);
        first.set(-1, 0, 3);
        first.set(-1, 1, 4);
        first.set(0, -2, 5);
        first.set(0, -1, 6);
        first.set(0, 0, 7);
        first.set(0, 1, 8);

        ReadableHeightmap map = new SimpleBinaryOperatorHeightmap(first, second, Integer::sum);

        int expectedValue = 2;
        for (int x = -1; x <= 0; x++)
            for (int y = -2; y <= 1; y++) {
                assertEquals(expectedValue, map.get(x, y));
                expectedValue++;
            }
    }
}
