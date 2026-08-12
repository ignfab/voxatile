package com.ignfab.minalac.generator.modules.luanti;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class LuantiVoxelTileTest {
    private static final LuantiVoxel GRASS = new LuantiVoxel("default:dirt_with_grass", (byte) 0, (byte) 0);
    private static final LuantiVoxel DIRT = new LuantiVoxel("default:dirt", (byte) 0, (byte) 0);
    private static final LuantiVoxel STONE = new LuantiVoxel("default:stone", (byte) 0, (byte) 0);

    private LuantiVoxelTile initTile(WorldBBox3d limits) {
        LuantiVoxelWorld world = assertDoesNotThrow(() -> new LuantiVoxelWorld(null));
        world.setLimits(limits);
        assertDoesNotThrow(world::initialize);
        return world.newTile(limits);
    }

    @Test
    public void testGetVoxel() {
        LuantiVoxelTile tile;

        tile = initTile(new WorldBBox3d(new WorldCoords3d(-1, -2, -5), new WorldCoords3d(2, 3, 6)));
        GRASS.place(tile, -1, -2, -3);
        STONE.place(tile, 0, -1, 1);
        DIRT.place(tile, -1, 0, 0);

        assertEquals(GRASS, tile.getVoxel(-1, -2, -3));
        assertEquals(STONE, tile.getVoxel(0, -1, 1));
        assertEquals(DIRT, tile.getVoxel(-1, 0, 0));

        tile = initTile(new WorldBBox3d(new WorldCoords3d(-5, -5, -20), new WorldCoords3d(50, 50, 500)));

        // Extremum of a map block [-16, -1]
        STONE.place(tile, -2, -3, -1);
        GRASS.place(tile, -2, -3, -16);
        // Extremum of another map block [16, 31]
        DIRT.place(tile, 37, 16, 387);
        STONE.place(tile, 37, 31, 387);

        // Testing on Luanti max limits
        assertEquals(STONE, tile.getVoxel(-2, -3, -1));
        assertEquals(GRASS, tile.getVoxel(-2, -3, -16));
        assertEquals(DIRT, tile.getVoxel(37, 16, 387));
        assertEquals(STONE, tile.getVoxel(37, 31, 387));
    }

    @Test
    public void testGetDefaultVoxel() {
        LuantiVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-5, -5, -20), new WorldCoords3d(50, 50, 500)));
        GRASS.place(tile, 1, 0, 0);

        // Block created with only one voxel
        assertEquals(LuantiVoxel.DEFAULT_VOXEL, tile.getVoxel(3, 2, 7));
        // Returns something even if the block is not created.
        assertEquals(LuantiVoxel.DEFAULT_VOXEL, tile.getVoxel(49, 49, 71));
    }
}
