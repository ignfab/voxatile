package com.ignfab.minalac.generator.generation.heightmaps.computed.operators;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CappedManhattanHeightmapOperatorTest {
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
        CappedManhattanHeightmapOperator operator = new CappedManhattanHeightmapOperator(3, 0);

        assertEquals(0, operator.compute(-4, 0, heightmap));

        // Top, bottom, left, right
        assertEquals(1, operator.compute(-5, 0, heightmap));
        assertEquals(1, operator.compute(-4, 1, heightmap));
        assertEquals(1, operator.compute(-4, -1, heightmap));
        assertEquals(1, operator.compute(-4, 1, heightmap));

        // Corner top-right, bottom-right, bottom left, top-left
        assertEquals(2, operator.compute(-5, 1, heightmap));
        assertEquals(2, operator.compute(-3, 1, heightmap));
        assertEquals(2, operator.compute(-3, -1, heightmap));
        assertEquals(2, operator.compute(-5, -1, heightmap));

        assertEquals(2, operator.compute(-2, 0, heightmap));
        assertEquals(3, operator.compute(-2, 1, heightmap));

        // With theoretical manhattan distance exceeding or equals to the maximum set
        assertEquals(3, operator.compute(-1, 0, heightmap)); // theoretical manhattan is 3
        assertEquals(3, operator.compute(-1, -2, heightmap)); // theoretical manhattan is 5
        assertEquals(5, new CappedManhattanHeightmapOperator(6, 0).compute(-1, -2, heightmap));

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

        CappedManhattanHeightmapOperator secondOperator = new CappedManhattanHeightmapOperator(3, 0);

        // With the new point, distance is 1 instead of 2 at (-3, -1) and (-5, -1)
        assertEquals(1, secondOperator.compute(-3, -1, secondHeightmap));
        assertEquals(1, secondOperator.compute(-5, -1, secondHeightmap));
    }
}
