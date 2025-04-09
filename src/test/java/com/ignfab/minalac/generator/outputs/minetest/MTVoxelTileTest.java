package com.ignfab.minalac.generator.outputs.minetest;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MTVoxelTileTest {
    private static MTVoxelTile tile;
    private static MTVoxel grass;
    private static MTVoxel dirt;
    private static MTVoxel stone;

    private void initWorld(WorldBBox3d limits) {
        MTVoxelWorld world = assertDoesNotThrow(() -> new MTVoxelWorld(null));
        world.setLimits(limits);
        assertDoesNotThrow(world::initialize);

        tile = world.newTile(limits);
        grass = new MTVoxel("default:dirt_with_grass", (byte) 0, (byte) 0);
        dirt = new MTVoxel("default:dirt", (byte) 0, (byte) 0);
        stone = new MTVoxel("default:stone", (byte) 0, (byte) 0);
    }

    @Test
    public void testGetVoxel() {
        initWorld(new WorldBBox3d(new WorldCoords3d(-1, -2, -5), new WorldCoords3d(2, 3, 6)));
        grass.place(tile, -1, -2, -3);
        stone.place(tile, 0, -1, 1);
        dirt.place(tile, -1, 0, 0);

        assertEquals(grass, tile.getVoxel(-1, -2, -3));
        assertEquals(stone, tile.getVoxel(0, -1, 1));
        assertEquals(dirt, tile.getVoxel(-1, 0, 0));

        initWorld(new WorldBBox3d(new WorldCoords3d(-5, -5, -20), new WorldCoords3d(50, 50, 500)));

        // Extremum of a map block [-16, -1]
        stone.place(tile, -2, -3, -1);
        grass.place(tile, -2, -3, -16);
        // Extremum of another map block [16, 31]
        dirt.place(tile, 37, 16, 387);
        stone.place(tile, 37, 31, 387);

        // Testing on MT max limits
        assertEquals(stone, tile.getVoxel(-2, -3, -1));
        assertEquals(grass, tile.getVoxel(-2, -3, -16));
        assertEquals(dirt, tile.getVoxel(37, 16, 387));
        assertEquals(stone, tile.getVoxel(37, 31, 387));
    }
}
