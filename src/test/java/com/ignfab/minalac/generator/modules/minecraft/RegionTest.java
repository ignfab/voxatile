package com.ignfab.minalac.generator.modules.minecraft;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.github.ensgijs.nbt.mca.TerrainChunk;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class RegionTest {
    @Test
    public void testGetOrCreateChunk() {
        Region region = new Region(0, 0);
        assertNull(region.file().getChunk(0, 0));

        TerrainChunk chunk = region.getOrCreateChunk(0, 0);
        assertNotNull(chunk);

        assertSame(chunk, region.getOrCreateChunk(0, 0));
        assertNotSame(chunk, region.getOrCreateChunk(0, 1));
        assertNotSame(chunk, region.getOrCreateChunk(1, 0));
    }

    @Test
    public void testSave(@TempDir File tempDir) {
        Region region = new Region(0, 0);
        region.getOrCreateChunk(0, 0);
        assertDoesNotThrow(() -> region.save(tempDir));
        File[] files = tempDir.listFiles();
        assertNotNull(files);
        assertEquals(1, files.length);
        File file = files[0];
        assertTrue(file.isFile());
        assertEquals("r.0.0.mca", file.getName());
        assertNotEquals(0, file.length());
    }

    @Test
    public void testComputeKey() {
        // Keys must be distinct from each other
        Set<Integer> keys = new HashSet<>();
        keys.add(Region.computeKey(-1, -1));
        keys.add(Region.computeKey(-1, 0));
        keys.add(Region.computeKey(-1, 1));
        keys.add(Region.computeKey(0, -1));
        keys.add(Region.computeKey(0, 0));
        keys.add(Region.computeKey(0, 1));
        keys.add(Region.computeKey(1, -1));
        keys.add(Region.computeKey(1, 0));
        keys.add(Region.computeKey(1, 1));
        assertEquals(9, keys.size());

        // And must be decodable
        assertKey(-1, -1);
        assertKey(-1, 0);
        assertKey(-1, 1);
        assertKey(0, -1);
        assertKey(0, 0);
        assertKey(0, 1);
        assertKey(1, -1);
        assertKey(1, 0);
        assertKey(1, 1);
    }

    private static void assertKey(int x, int z) {
        int key = Region.computeKey(x, z);
        assertEquals(x, Region.keyToRegionX(key));
        assertEquals(z, Region.keyToRegionZ(key));
    }
}
