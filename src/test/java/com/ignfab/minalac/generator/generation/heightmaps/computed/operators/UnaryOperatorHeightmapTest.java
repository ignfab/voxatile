package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnaryOperatorHeightmapTest {
    @Test
    public void testSimpleGet() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 4);
        Heightmap heightmap = new Heightmap(bbox, 0);

        heightmap.set(-1, -2, 1);
        heightmap.set(-1, -1, 2);
        heightmap.set(-1, 0, 3);
        heightmap.set(-1, 1, 4);
        heightmap.set(0, -2, 5);
        heightmap.set(0, -1, 6);
        heightmap.set(0, 0, 7);
        heightmap.set(0, 1, 8);

        UnaryHeightmapOperator operator = new UnaryHeightmapOperator.Simple((x) -> 2 * x);

        int expectedValue = 1;
        for (int x = -1; x <= 0; x++)
            for (int y = -2; y <= 1; y++) {
                assertEquals(2 * expectedValue, operator.compute(x, y, heightmap));
                expectedValue++;
            }
    }
}
