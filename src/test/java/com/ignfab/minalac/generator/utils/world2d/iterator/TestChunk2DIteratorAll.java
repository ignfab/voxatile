package com.ignfab.minalac.generator.utils.world2d.iterator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ignfab.minalac.generator.utils.world2d.chunk.ArrayChunk2d;

public class TestChunk2DIteratorAll {
    @Test
    public void testFullyFilledMapIterator() {
        ArrayChunk2d chunk = new ArrayChunk2d(1, 2, 3, 4, 1);
        //Fill chunk with unique values
        for (int x = 1; x <= 3; x++)
            for (int y = 2; y <= 5; y++)
                chunk.set(x, y, x + y * 3);

        Chunk2dIteratorAll iterator = new Chunk2dIteratorAll(chunk);
        boolean[][] done = {{false, false, false, false}, {false, false, false, false}, {false, false, false, false}};
        int count = 0;

        // Check if iterator gives the right value
        while (iterator.hasNext()) {
            Chunk2dElement element = iterator.next();
            assertEquals(String.format("Wrong chunk value at (x = %d, y = %d)", element.getX(), element.getY()), element.getX() + element.getY() * 3, element.getValue());
            done[element.getX() - 1][element.getY() - 2] = true;
            count++;
        }

        // Check if the iterator has visited every place exactly once.
        assertEquals("Unexpected number of iterated element", 12, count);
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 4; y++)
                assertTrue(String.format("Chunk element at (x = %d, y = %d) skipped !", x + 1, y + 2), done[x][y]);
    }
}