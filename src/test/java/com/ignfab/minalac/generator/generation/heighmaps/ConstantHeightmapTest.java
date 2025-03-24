package com.ignfab.minalac.generator.generation.heighmaps;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.ConstantHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConstantHeightmapTest {
    @Test
    public void testGet() {
        WorldBBox2d bbox = new WorldBBox2d(-3, -2, 3, 5);
        ReadableHeightmap map = new ConstantHeightmap(-7, bbox);
        for (int x = -3; x < 0; x++)
            for (int y = -2; y < 3; y++)
                assertEquals(-7, map.get(x, y));

        assertThrows(IndexOutOfBoundsException.class, () -> map.get(-4, -2));
        assertThrows(IndexOutOfBoundsException.class, () -> map.get(-1, 4));
    }
}
