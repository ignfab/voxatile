package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

public class WorldBBox2dIteratorTest {
    @Test
    public void testIterator() {
        WorldBBox2d bbox = new WorldBBox2d(1, 2, 3, 4);

        WorldBBox2dIterator iterator = new WorldBBox2dIterator(bbox);
        boolean[][] done = new boolean[3][4];
        int count = 0;

        // Check if iterator gives the right values.
        while (iterator.hasNext()) {
            WorldCoords2d coord = iterator.next();
            assertTrue(1 <= coord.x() && coord.x() <= 3, String.format("x = %d is out of box", coord.x()));
            assertTrue(2 <= coord.y() && coord.y() <= 5, String.format("y = %d is out of box", coord.y()));
            done[coord.x() - 1][coord.y() - 2] = true;
            count++;
        }

        assertThrows(NoSuchElementException.class, () -> iterator.next());

        // Check if the iterator has visited every place exactly once.
        assertEquals(12, count, "Unexpected number of iterated element");
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 4; y++)
                assertTrue(done[x][y], String.format("Coordinates (x = %d, y = %d) skipped !", x + 1, y + 2));
    }

    @Test
    public void testIteratorOnEmptyBox() {
        WorldBBox2d bbox = new WorldBBox2d(1, 2, 0, 0);
        WorldBBox2dIterator iterator = new WorldBBox2dIterator(bbox);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
}
