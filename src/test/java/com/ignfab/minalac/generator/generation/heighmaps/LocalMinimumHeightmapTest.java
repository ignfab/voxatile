package com.ignfab.minalac.generator.generation.heighmaps;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.LocalMinimumHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

import static org.junit.jupiter.api.Assertions.*;

public class LocalMinimumHeightmapTest {

    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new LocalMinimumHeightmap(
            new Heightmap(-1, 2, 3, 4, 1),
            -1
        ));

        assertDoesNotThrow(() -> new LocalMinimumHeightmap(
            new Heightmap(-1, 2, 3, 4, 1),
            0
        ));

        assertDoesNotThrow(() -> new LocalMinimumHeightmap(
            new Heightmap(-1, 2, 3, 4, 1),
            3
        ));
    }

    @Test
    public void testGet() {
        Heightmap heightmap = new Heightmap(-5, -2, 5, 4, 7);

        heightmap.set(-4, 0, 1);
        heightmap.set(-5, 1, 0);
        heightmap.set(-2, 1, -1);
        /*
        +--------------y-->
        |  7   7   7   0 -5
        |  7   7   1   7 -4
        |  7   7   7   7 -3
        |  7   7   7  -1 -2
        |  7   7   7   7 -1
        x -2  -1   0   1
        |
        v
        */

        ReadableHeightmap rangeOne = new LocalMinimumHeightmap(heightmap, 1);
        assertEquals(0, rangeOne.get(-4, 0));
        assertEquals(1, rangeOne.get(-3, -1));
        assertEquals(-1, rangeOne.get(-2, 0));
        assertEquals(7, rangeOne.get(-2, -1));

        ReadableHeightmap rangeTwo = new LocalMinimumHeightmap(heightmap, 2);
        assertEquals(-1, rangeTwo.get(-4, -1));
        assertEquals(1, rangeTwo.get(-5, -2));
    }
}
