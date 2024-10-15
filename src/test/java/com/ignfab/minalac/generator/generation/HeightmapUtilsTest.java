package com.ignfab.minalac.generator.generation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapUtilsTest {
    @Test
    public void testApplyAverageFilter() {
        Heightmap heightmap = new Heightmap(-5, -2, 6, 3, 0);
        /*
        +-----------y->
        |  3  4  3 -5
        |  5  8  1 -4
        |  0  0  0 -3
        |  1  3  4 -2
        |  5  6  7 -1
        |  3  8  6  0
        x -2 -1  0
        |
        v
        */
        heightmap.set(-5, -2, 3);
        heightmap.set(-4, -2, 5);
        heightmap.set(-2, -2, 1);
        heightmap.set(-1, -2, 5);
        heightmap.set(0, -2, 3);

        heightmap.set(-5, -1, 4);
        heightmap.set(-4, -1, 8);
        heightmap.set(-2, -1, 3);
        heightmap.set(-1, -1, 6);
        heightmap.set(0, -1, 8);

        heightmap.set(-5, 0, 3);
        heightmap.set(-4, 0, 1);
        heightmap.set(-2, 0, 4);
        heightmap.set(-1, 0, 7);
        heightmap.set(0, 0, 6);

        HeightmapUtils.applyAverageFilter(heightmap);

        Heightmap expected = new Heightmap(-5, -2, 6, 3, 0);
        /*
        +-----------y->
        |  2  2  1 -5
        |  2  2  1 -4
        |  0  0  0 -3
        |  1  2  2 -2
        |  2  4  3 -1
        |  2  3  3  0
        x -2 -1  0
        |
        v
        */
        expected.set(-5, -2, 2);
        expected.set(-4, -2, 2);
        expected.set(-2, -2, 1);
        expected.set(-1, -2, 2);
        expected.set(0, -2, 2);

        expected.set(-5, -1, 2);
        expected.set(-4, -1, 2);
        expected.set(-2, -1, 2);
        expected.set(-1, -1, 4);
        expected.set(0, -1, 3);

        expected.set(-5, 0, 1);
        expected.set(-4, 0, 1);
        expected.set(-2, 0, 2);
        expected.set(-1, 0, 3);
        expected.set(0, 0, 3);

        for (int x = heightmap.bbox().minX(); x <= heightmap.bbox().maxX(); x++)
            for (int y = heightmap.bbox().minY(); y <= heightmap.bbox().maxY(); y++)
                assertEquals(expected.get(x, y), heightmap.get(x, y), String.format("x=%d y=%d", x, y));

        // TODO-PR: unit test for lock when updating?
    }
}
