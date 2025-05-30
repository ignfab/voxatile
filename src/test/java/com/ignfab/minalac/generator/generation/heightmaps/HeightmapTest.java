package com.ignfab.minalac.generator.generation.heightmaps;


import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapTest {
    private void setValues(Heightmap h, int[] v) {
        assertEquals(h.bbox().size().area(), v.length);
        int i = 0;
        for (int y = h.bbox().minY(); y <= h.bbox().maxY(); y++)
            for (int x = h.bbox().minX(); x <= h.bbox().maxX(); x++)
                h.set(x, y, v[i++]);
    }

    private void assertValues(int[] v, Heightmap h) {
        assertEquals(v.length, h.bbox().size().area(), "Heightmap area should be as expected");

        int i = 0;
        for (int y = h.bbox().minY(); y <= h.bbox().maxY(); y++)
            for (int x = h.bbox().minX(); x <= h.bbox().maxX(); x++)
                assertEquals(v[i++], h.get(x, y), "Value should match at (%d, %d)".formatted(x, y));
    }

    @Test
    public void testGet() {
        Heightmap heightmap1 = new Heightmap(0, 0, 3, 2, 0);
        setValues(heightmap1, new int[]{
            1, 2, 3,
            4, 5, 6
        });

        assertEquals(2, heightmap1.get(1, 0));
        assertEquals(3, heightmap1.get(2, 0));
        assertEquals(6, heightmap1.get(2, 1));

        Heightmap heightmap2 = new Heightmap(-5, -2, 3, 2, 0);
        setValues(heightmap2, new int[]{
            1, 2, 3,
            4, 5, 6
        });

        assertEquals(1, heightmap2.get(-5, -2));
        assertEquals(2, heightmap2.get(-4, -2));
        assertEquals(4, heightmap2.get(-5, -1));
        assertEquals(5, heightmap2.get(-4, -1));
        assertEquals(6, heightmap2.get(-3, -1));
    }

    @Test
    public void testSet() {
        Heightmap heightmap = new Heightmap(0, 0, 3, 2, 0);
        setValues(heightmap, new int[]{ 1, 2, 3, 4, 5, 6 });

        heightmap.set(0, 1, 7);
        heightmap.set(1, 1, 8);
        heightmap.set(2, 0, 9);

        assertEquals(7, heightmap.get(0, 1));
        assertEquals(8, heightmap.get(1, 1));
        assertEquals(9, heightmap.get(2, 0));
    }

    @Test
    public void testOut() {
        Heightmap heightmap = new Heightmap(0, 0, 3, 2, 0);
        assertThrows(IndexOutOfBoundsException.class, () -> heightmap.set(25, 25, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> heightmap.get(25, 25));
    }

    @Test
    public void testCopy() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);
        Heightmap heightmap = new Heightmap(bbox, 0);
        setValues(heightmap, new int[]{ 1, 2, 3, 4, 5, 6 });

        Heightmap copy = heightmap.copy();
        heightmap.set(-1, -2, 7);

        assertNotSame(heightmap, copy);
        assertEquals(heightmap.bbox(), copy.bbox());
        assertValues(new int[]{ 1, 2, 3, 4, 5, 6 }, copy);
    }

    @Test
    public void testCopyValues() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);
        Heightmap other;

        Heightmap heightmap = new Heightmap(bbox, 0);
        setValues(heightmap, new int[]{ 7, 7, 7, 7, 7, 7 });

        // Copy with same bbox
        other = new Heightmap(bbox, 0);
        setValues(other, new int[]{ 1, 2, 3, 4, 5, 6 });

        heightmap.copyValues(other);
        assertValues(new int[]{ 1, 2, 3, 4, 5, 6 }, heightmap);

        // Copy with non intersecting bbox
        other = new Heightmap(new WorldBBox2d(3, 4, 5, 6), 7);

        heightmap.copyValues(other);
        assertValues(new int[]{ 1, 2, 3, 4, 5, 6 }, heightmap);

        // Copy with intersecting bbox
        other = new Heightmap(new WorldBBox2d(-2, -3, 2, 3), 7);
        setValues(other, new int[]{ -1, -2, -3, -4, -5, -6 });

        // [] = heightmap, {} = other
        //    x: -2 -1  0
        //  y:
        // -3   {-1 -2}
        // -2   {-3[-4} 2]
        // -1   {-5[-6} 4]
        //  0      [ 5  6]

        heightmap.copyValues(other);
        assertValues(new int[]{ -4, 2, -6, 4, 5, 6 }, heightmap);
    }
}
