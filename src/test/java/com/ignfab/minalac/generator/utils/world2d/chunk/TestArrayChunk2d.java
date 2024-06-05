package com.ignfab.minalac.generator.utils.world2d.chunk;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class TestArrayChunk2d {
    private int[] getData(ArrayChunk2d chunk) throws NoSuchFieldException, IllegalAccessException {
        Field data = ArrayChunk2d.class.getDeclaredField("values");
        data.setAccessible(true);
        return (int[]) data.get(chunk);
    }

    private void replaceValuesField(ArrayChunk2d chunk, int[] tab) throws NoSuchFieldException, IllegalAccessException {
        Field data = ArrayChunk2d.class.getDeclaredField("values");
        data.setAccessible(true);
        data.set(chunk, tab);
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        ArrayChunk2d chunk1 = new ArrayChunk2d(0, 0, 3, 2, 0);
        int[] newValues = { 1, 2, 3, 4, 5, 6 };
        /*
        | - - y ->
        | 1 2
        | 3 4
        | 5 6
        x
        |
        v
        */
        replaceValuesField(chunk1, newValues);

        assertEquals(2, chunk1.get(0, 1));
        assertEquals(3, chunk1.get(1, 0));
        assertEquals(6, chunk1.get(2, 1));

        ArrayChunk2d chunk2 = new ArrayChunk2d(-5, -2, 3, 2, 0);
        replaceValuesField(chunk2, newValues);

        /*
        |  -  -   y ->
        |  1  2  -5
        |  3  4  -4
        |  5  6  -3
        x -2 -1
        |
        v
        */

        assertEquals(1, chunk2.get(-5, -2));
        assertEquals(2, chunk2.get(-5, -1));
        assertEquals(5, chunk2.get(-3, -2));
        assertEquals(6, chunk2.get(-3, -1));
        assertEquals(4, chunk2.get(-4, -1));
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        ArrayChunk2d chunk = new ArrayChunk2d(0, 0, 3, 2, 0);
        int[] newValues = {1, 2, 3, 4, 5, 6};
        /*
        | - - y ->
        | 1 2
        | 3 4
        | 5 6
        x
        |
        v
        */

        replaceValuesField(chunk, newValues);

        chunk.set(0, 1, 7);
        chunk.set(1, 1, 8);
        chunk.set(2, 0, 9);

        assertEquals(7, getData(chunk)[1]);
        assertEquals(8, getData(chunk)[3]);
        assertEquals(9, getData(chunk)[4]);
    }

    @Test
    public void testOut() {
        ArrayChunk2d chunk = new ArrayChunk2d(0, 0, 3, 2, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> chunk.set(25, 25, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> chunk.get(25, 25));
    }
}
