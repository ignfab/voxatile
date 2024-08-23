package com.ignfab.minalac.generator.utils.world2d.iterator;
/*
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestChunk2DIteratorSkip {
    @Test
    public void testFullyFilledMapIterator() {
        ArrayChunk2d chunk = new ArrayChunk2d(1, 2, 3, 4, 1);
        // Fill chunk with unique values
        for (int x = 1; x <= 3; x++)
            for (int y = 2; y <= 5; y++)
                chunk.set(x, y, x + y * 3);

        Chunk2dIteratorSkip iterator = new Chunk2dIteratorSkip(chunk, -1);
        boolean[][] done = {{false, false, false, false}, {false, false, false, false}, {false, false, false, false}};
        int count = 0;

        // Check if iterator gives the right value.
        while (iterator.hasNext()) {
            Chunk2dElement element = iterator.next();
            assertEquals(element.getX() + element.getY() * 3, element.getValue(),
                String.format("Wrong chunk value at (x = %d, y = %d)", element.getX(), element.getY()));
            done[element.getX() - 1][element.getY() - 2] = true;
            count++;
        }

        // Check if the iterator has visited every place exactly once.
        assertEquals(12, count, "Unexpected number of iterated element");
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 4; y++)
                assertTrue(done[x][y],
                    String.format("Chunk element at (x = %d, y = %d) skipped !", x + 1, y + 2));
    }

    @Test
    public void testPartiallyFilledChunkIterator() {
        ArrayChunk2d chunk = new ArrayChunk2d(2, 3, 4, 5, 0);
        // Fill a 2*3 rectangle in the middle of the chunk
        for (int x = 3; x <= 4; x++)
            for (int y = 4; y <= 6; y++)
                chunk.set(x, y, 1);

        Chunk2dIteratorSkip iterator = new Chunk2dIteratorSkip(chunk, 0);
        boolean[][] done = {{false, false, false}, {false, false, false}};
        int count = 0;

        // Check we only iterate over the filled rectangle
        while (iterator.hasNext()) {
            Chunk2dElement element = iterator.next();
            assertTrue(3 <= element.getX() && element.getX() <= 4 && 4 <= element.getY() && element.getY() <= 6,
                String.format("Iterated outside set chunk at (x = %d, y = %d)", element.getX(), element.getY()));
            assertEquals(1, element.getValue(), "Wrong value");
            done[element.getX() - 3][element.getY() - 4] = true;
            count++;
        }

        // Check if the iterator has visited every place exactly once.
        assertEquals(6, count, "Unexpected number of iterated element");
        for (int x = 0; x < 2; x++)
            for (int y = 0; y < 3; y++)
                assertTrue(done[x][y],
                    String.format("Chunk element at (x = %d, y = %d) skipped !", x + 2 + 1, y + 3 + 1));
    }
}
*/
