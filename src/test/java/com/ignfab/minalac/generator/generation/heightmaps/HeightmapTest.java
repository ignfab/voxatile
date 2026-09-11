package com.ignfab.minalac.generator.generation.heightmaps;


import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapTest {
    private void setValues(Heightmap h, WorldBBox2d bbox, int[] v) {
        assertEquals(bbox.size().area(), v.length);
        int i = 0;
        for (int y = bbox.minY(); y <= bbox.maxY(); y++)
            for (int x = bbox.minX(); x <= bbox.maxX(); x++)
                h.set(x, y, v[i++]);
    }

    private void assertValues(WorldBBox2d bbox, int[] v, Heightmap h) {
        assertEquals(v.length, bbox.size().area());

        int i = 0;
        for (int y = bbox.minY(); y <= bbox.maxY(); y++)
            for (int x = bbox.minX(); x <= bbox.maxX(); x++)
                assertEquals(v[i++], h.get(x, y), "Value should match at (%d, %d)".formatted(x, y));
    }

    @Test
    public void testGet() {
        WorldBBox2d bbox;

        bbox = new WorldBBox2d(0, 0, 3, 2);
        Heightmap heightmap1 = new Heightmap(bbox, 0);
        setValues(heightmap1, bbox, new int[]{
            1, 2, 3,
            4, 5, 6
        });

        assertEquals(2, heightmap1.get(1, 0));
        assertEquals(3, heightmap1.get(2, 0));
        assertEquals(6, heightmap1.get(2, 1));

        bbox = new WorldBBox2d(-5, -2, 3, 2);
        Heightmap heightmap2 = new Heightmap(bbox, 0);
        setValues(heightmap2, bbox, new int[]{
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
        WorldBBox2d bbox = new WorldBBox2d(0, 0, 3, 2);
        Heightmap heightmap = new Heightmap(bbox, 0);
        setValues(heightmap, bbox, new int[]{ 1, 2, 3, 4, 5, 6 });

        heightmap.set(0, 1, 7);
        heightmap.set(1, 1, 8);
        heightmap.set(2, 0, 9);

        assertEquals(7, heightmap.get(0, 1));
        assertEquals(8, heightmap.get(1, 1));
        assertEquals(9, heightmap.get(2, 0));
    }

    @Test
    public void testOut() {
        WorldBBox2d bbox = new WorldBBox2d(0, 0, 3, 2);
        Heightmap heightmap = new Heightmap(bbox, 23);
        assertThrows(IndexOutOfBoundsException.class, () -> heightmap.set(25, 25, 0));
        assertEquals(23, heightmap.get(25, 25));
    }

    @Test
    public void testCopy() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);
        Heightmap heightmap = new Heightmap(bbox, 0);
        setValues(heightmap, bbox, new int[]{ 1, 2, 3, 4, 5, 6 });

        Heightmap copy = heightmap.copy();
        heightmap.set(-1, -2, 7);

        assertNotSame(heightmap, copy);
        assertValues(bbox, new int[]{ 1, 2, 3, 4, 5, 6 }, copy);
    }

    @Test
    public void testCopyValues() {
        WorldBBox2d bbox = new WorldBBox2d(-1, -2, 2, 3);
        Heightmap other;

        Heightmap heightmap = new Heightmap(bbox, 0);
        setValues(heightmap, bbox, new int[]{ 7, 7, 7, 7, 7, 7 });

        other = new Heightmap(bbox, 0);
        setValues(other, bbox, new int[]{ 1, 2, 3, 4, 5, 6 });

        heightmap.copyValues(other);
        assertValues(bbox, new int[]{ 1, 2, 3, 4, 5, 6 }, heightmap);
    }
}
