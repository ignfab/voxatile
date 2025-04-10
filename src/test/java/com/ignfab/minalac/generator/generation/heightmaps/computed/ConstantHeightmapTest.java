package com.ignfab.minalac.generator.generation.heightmaps.computed;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantHeightmapTest {
    @Test
    public void testGet() {
        ReadableHeightmap map = new ConstantHeightmap(-7).create(null);
        assertEquals(-7, map.get(0, 0));
        assertEquals(-7, map.get(-123, 456));
        assertEquals(-7, map.get(986, -543));
    }
}
