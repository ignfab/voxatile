package com.ignfab.minalac.generator.outputs.testing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class TestingVoxelTileTest {
    @Test
    @DisplayName("Ensure that everything that is stored in the world can be retreived")
    public void testSetAndGet() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(-1, -2, -3, 5, 4, 3));
        // Fill the world with unique values
        for (int x = -1; x <= 3; x++)
            for (int y = -2; y <= 1; y++)
                for (int z = -3; z <= -1; z++)
                    tile.set(x, y, z, x + " " + y + " " + z);

        // Test values are retrieved
        for (int z = -3; z <= -1; z++)
            for (int y = -2; y <= 1; y++)
                for (int x = -1; x <= 3; x++)
                    tile.assertVoxel(x + " " + y + " " + z, x, y, z);
    }

    @Test
    @DisplayName("Test setting in the world using voxel can be retrieved")
    public void testGetVoxel() {
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(1, 2, 3, 4, 5, 6));
        TestingVoxel vt1 = new TestingVoxel("AA");
        TestingVoxel vt2 = new TestingVoxel("BB");

        vt1.place(tile, 1, 2, 3);
        vt2.place(tile, 3, 4, 5);
        tile.voxels().assertVoxel("AA", 1, 2, 3);
        tile.voxels().assertVoxel("BB", 3, 4, 5);

        assertEquals(vt1, tile.voxels().getVoxel(1, 2, 3));
        assertEquals(vt2, tile.voxels().getVoxel(3, 4, 5));
        assertNull(tile.voxels().getVoxel(0, 0, 0));
        assertNull(tile.voxels().getVoxel(3, 2, 5));
    }

    @Test
    @DisplayName("Just ensure save throws no exception")
    public void testSave() {
        TestingVoxelTile tile = new TestingVoxelTile(new WorldBBox3d(0, 0, 0, 1, 1, 1));
        assertDoesNotThrow(() -> tile.save());
    }
}
