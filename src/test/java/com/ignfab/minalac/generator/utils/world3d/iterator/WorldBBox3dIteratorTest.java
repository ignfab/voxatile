package com.ignfab.minalac.generator.utils.world3d.iterator;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dIteratorTest {
    @Test
    public void testIterator() {
        WorldBBox3d bbox = new WorldBBox3d(1, 2, 3, 4, 3, 2);

        WorldBBox3dIterator iterator = new WorldBBox3dIterator(bbox);
        boolean[][][] done = new boolean[4][3][2];
        int count = 0;

        // Check if iterator gives the right values.
        while (iterator.hasNext()) {
            WorldCoords3d coord = iterator.next();
            assertTrue(1 <= coord.x() && coord.x() <= 4, String.format("x = %d is out of box", coord.x()));
            assertTrue(2 <= coord.y() && coord.y() <= 4, String.format("y = %d is out of box", coord.y()));
            assertTrue(3 <= coord.z() && coord.z() <= 4, String.format("z = %d is out of box", coord.z()));
            done[coord.x() - 1][coord.y() - 2][coord.z() - 3] = true;
            count++;
        }

        assertThrows(NoSuchElementException.class, () -> iterator.next());

        // Check if the iterator has visited every place exactly once.
        assertEquals(24, count, "Unexpected number of iterated element");
        for (int x = 0; x < 4; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 2; z++)
                    assertTrue(done[x][y][z], String.format("Coordinates (x = %d, y = %d, z = %d) skipped !", x + 1, y + 2, z + 3));
    }

    @Test
    public void testIteratorOnEmptyBox() {
        WorldBBox3d bbox = new WorldBBox3d(1, 2, 3, 0, 0, 0);
        WorldBBox3dIterator iterator = new WorldBBox3dIterator(bbox);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

}
