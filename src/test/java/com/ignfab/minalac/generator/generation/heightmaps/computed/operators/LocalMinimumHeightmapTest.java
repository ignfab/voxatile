package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;

import static org.junit.jupiter.api.Assertions.*;

public class LocalMinimumHeightmapTest {

    @Test
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new LocalMinimumHeightmapOperator(-1));
        assertDoesNotThrow(() -> new LocalMinimumHeightmapOperator(0));
        assertDoesNotThrow(() -> new LocalMinimumHeightmapOperator(3));
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

        LocalMinimumHeightmapOperator rangeOne = new LocalMinimumHeightmapOperator(1);
        assertEquals(0, rangeOne.compute(-4, 0, heightmap));
        assertEquals(1, rangeOne.compute(-3, -1, heightmap));
        assertEquals(-1, rangeOne.compute(-2, 0, heightmap));
        assertEquals(7, rangeOne.compute(-2, -1, heightmap));

        LocalMinimumHeightmapOperator rangeTwo = new LocalMinimumHeightmapOperator(2);
        assertEquals(-1, rangeTwo.compute(-4, -1, heightmap));
        assertEquals(1, rangeTwo.compute(-5, -2, heightmap));
    }
}
