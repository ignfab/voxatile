package com.ignfab.minalac.generator.utils.world2d.iterator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

public class TestWorldBBox2dIterator {
    @Test
    public void testIterator() {
        WorldBBox2d bbox = new WorldBBox2d(1, 2, 3, 4);

        WorldBBox2dIterator iterator = new WorldBBox2dIterator(bbox);
        boolean[][] done = {{false, false, false, false}, {false, false, false, false}, {false, false, false, false}};
        int count = 0;

        // Check if iterator gives the right values.
        while (iterator.hasNext()) {
            WorldCoords2d coord = iterator.next();
            assertTrue(String.format("x = %d is out of box", coord.getX()), 1 <= coord.getX() && coord.getX() <= 3);
            assertTrue(String.format("y = %d is out of box", coord.getY()), 2 <= coord.getY() && coord.getY() <= 5);
            done[coord.getX() - 1][coord.getY() - 2] = true;
            count++;
        }

        // Check if the iterator has visited every place exactly once.
        assertEquals("Unexpected number of iterated element", 12, count);
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 4; y++)
                assertTrue(String.format("Coordinates (x = %d, y = %d) skipped !", x + 1, y + 2), done[x][y]);
    }
}