package com.ignfab.minalac.generator.generation.heighmaps;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.CappedManhattanHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CappedManhattanHeightmapTest {
    @Test
    public void testGet() {
        Heightmap heightmap = new Heightmap(-5, -2, 5, 4, 7);

        heightmap.set(-4, 0, 0);
        /*
        +-----------y-->
        |  7  7  7  7 -5
        |  7  7  0  7 -4
        |  7  7  7  7 -3
        |  7  7  7  7 -2
        |  7  7  7  7 -1
        x -2 -1  0  1
        |
        v
        */

        ReadableHeightmap manhattan = new CappedManhattanHeightmap(heightmap, 3, 0);

        assertEquals(0, manhattan.get(-4, 0));

        // Top, bottom, left, right
        assertEquals(1, manhattan.get(-5, 0));
        assertEquals(1, manhattan.get(-4, 1));
        assertEquals(1, manhattan.get(-4, -1));
        assertEquals(1, manhattan.get(-4, 1));

        // Corner top-right, bottom-right, bottom left, top-left
        assertEquals(2, manhattan.get(-5, 1));
        assertEquals(2, manhattan.get(-3, 1));
        assertEquals(2, manhattan.get(-3, -1));
        assertEquals(2, manhattan.get(-5, -1));

        assertEquals(2, manhattan.get(-2, 0));
        assertEquals(3, manhattan.get(-2, 1));

        // With theoretical manhattan distance exceeding or equals to the maximum set
        assertEquals(3, manhattan.get(-1, 0)); // theoretical manhattan is 3
        assertEquals(3, manhattan.get(-1, -2)); // theoretical manhattan is 5
        assertEquals(5, new CappedManhattanHeightmap(heightmap, 6, 0).get(-1, -2));

        Heightmap secondHeightmap = new Heightmap(-5, -2, 5, 4, 7);

        secondHeightmap.set(-4, 0, 0);
        secondHeightmap.set(-4, -1, 0);
        /*
        +-----------y-->
        |  7  7  7  7 -5
        |  7  0  0  7 -4
        |  7  7  7  7 -3
        |  7  7  7  7 -2
        |  7  7  7  7 -1
        x -2 -1  0  1
        |
        v
        */

        ReadableHeightmap secondManhattan = new CappedManhattanHeightmap(secondHeightmap, 3, 0);

        // With the new point, distance is 1 instead of 2 at (-3, -1) and (-5, -1)
        assertEquals(1, secondManhattan.get(-3, -1));
        assertEquals(1, secondManhattan.get(-5, -1));
    }
}
