package com.ignfab.minalac.generator.generation.heighmaps;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapTest {
    private static final int[] TEST_VALUES = { 1, 2, 3, 4, 5, 6 };

    private int[] getValuesField(Heightmap heightmap) throws NoSuchFieldException, IllegalAccessException {
        Field valuesField = Heightmap.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        return (int[]) valuesField.get(heightmap);
    }

    private void setValuesField(Heightmap heightmap, int[] tab) throws NoSuchFieldException, IllegalAccessException {
        Field valuesField = Heightmap.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        valuesField.set(heightmap, tab);
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        Heightmap heightmap1 = new Heightmap(0, 0, 3, 2, 0);
        setValuesField(heightmap1, TEST_VALUES);

        /*
        +-----y-->
        | 1 2
        | 3 4
        | 5 6
        x
        |
        v
        */

        assertEquals(2, heightmap1.get(0, 1));
        assertEquals(3, heightmap1.get(1, 0));
        assertEquals(6, heightmap1.get(2, 1));

        Heightmap heightmap2 = new Heightmap(-5, -2, 3, 2, 0);
        setValuesField(heightmap2, TEST_VALUES);

        /*
        +---------y-->
        |  1  2  -5
        |  3  4  -4
        |  5  6  -3
        x -2 -1
        |
        v
        */

        assertEquals(1, heightmap2.get(-5, -2));
        assertEquals(2, heightmap2.get(-5, -1));
        assertEquals(5, heightmap2.get(-3, -2));
        assertEquals(6, heightmap2.get(-3, -1));
        assertEquals(4, heightmap2.get(-4, -1));
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        Heightmap heightmap = new Heightmap(0, 0, 3, 2, 0);
        setValuesField(heightmap, TEST_VALUES);

        /*
        +-----y-->
        | 1 2
        | 3 4
        | 5 6
        x
        |
        v
        */

        heightmap.set(0, 1, 7);
        heightmap.set(1, 1, 8);
        heightmap.set(2, 0, 9);

        int[] values = getValuesField(heightmap);
        assertEquals(7, values[1]);
        assertEquals(8, values[3]);
        assertEquals(9, values[4]);
    }

    @Test
    public void testOut() {
        Heightmap heightmap = new Heightmap(0, 0, 3, 2, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> heightmap.set(25, 25, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> heightmap.get(25, 25));
    }

    @Test
    public void testCopy() throws NoSuchFieldException, IllegalAccessException {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);
        Heightmap heightmap = new Heightmap(bbox, 0);

        heightmap.set(-1, -2, 1);
        heightmap.set(-1, -1, 2);
        heightmap.set(-1, 0, 3);
        heightmap.set(0, -2, 4);
        heightmap.set(0, -1, 5);
        heightmap.set(0, 0, 6);

        Heightmap copy = heightmap.copy();

        assertNotSame(heightmap, copy);

        assertEquals(heightmap.bbox(), copy.bbox());

        int expectedValue = 1;
        for (int x = -1; x <= 0; x++)
            for (int y = -2; y <= 0; y++) {
                assertEquals(expectedValue, copy.get(x, y));
                expectedValue++;
            }
        assertNotSame(getValuesField(heightmap), getValuesField(copy));
    }

    @Test
    public void testSwap() throws NoSuchFieldException, IllegalAccessException {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);

        Heightmap heightmap = new Heightmap(bbox, 0);
        int[] valuesHeightmap = { 7, 7, 7, 7, 7, 7 };
        setValuesField(heightmap, valuesHeightmap);

        Heightmap other = new Heightmap(bbox, 0);
        int[] valuesOther = { 1, 2, 3, 4, 5, 6 };
        setValuesField(other, valuesOther);

        Heightmap otherDifferentBbox = new Heightmap(new WorldBBox2d(0, -2, 5, 4), 7);

        assertThrows(IllegalArgumentException.class, () -> heightmap.swap(otherDifferentBbox));
        assertDoesNotThrow(() -> heightmap.swap(other));

        assertSame(valuesOther, getValuesField(heightmap));
        assertSame(valuesHeightmap, getValuesField(other));
    }
}
