package com.ignfab.minalac.generator.extensions.minecraft;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelTileTest {
    private static final MCVoxel STONE = new MCVoxel("minecraft:stone");

    private MCVoxelTile initTile(WorldBBox3d limits) {
        MCVoxelWorld world = assertDoesNotThrow(() -> new MCVoxelWorld(null));
        world.setLimits(limits);
        assertDoesNotThrow(world::initialize);
        return world.newTile(limits);
    }

    @Test
    public void testIsOutOfLimits() throws MapWriteException {
        MCVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-10, -20, 0), new WorldCoords3d(20, 30, 40)));

        // X/Y/Z => X/Z/-Y
        assertFalse(tile.isOutOfLimits(-10, 0, 19));
        assertFalse(tile.isOutOfLimits(20, 40, -31));

        assertFalse(tile.isOutOfLimits(5, 5, -21));

        assertTrue(tile.isOutOfLimits(21, 40, -31));
        assertTrue(tile.isOutOfLimits(20, 41, -31));
        assertTrue(tile.isOutOfLimits(20, 40, -32));
        assertTrue(tile.isOutOfLimits(-11, 0, 19));
        assertTrue(tile.isOutOfLimits(-10, -1, 19));
        assertTrue(tile.isOutOfLimits(-10, 0, 20));
    }

    @Test
    public void testGetVoxel() throws MapWriteException {
        MCVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-20, -20, 0), new WorldCoords3d(50, 50, 255)));
        MCVoxel barrel = new MCVoxel("minecraft:barrel", Map.of(
            "facing", "south",
            "open", "true"
        ));
        MCVoxel dirt = new MCVoxel("minecraft:dirt");
        barrel.place(tile, -9, -8, 1);
        STONE.place(tile, 4, -3, 78);
        dirt.place(tile, -7, 5, 55);

        assertEquals(barrel, tile.getVoxel(-9, -8, 1));
        assertEquals(STONE, tile.getVoxel(4, -3, 78));
        assertEquals(dirt, tile.getVoxel(-7, 5, 55));

        // Minecraft zMax
        dirt.place(tile, 3, 4, 255);
        assertEquals(dirt, tile.getVoxel(3, 4, 255));
        // Minecraft zMin
        barrel.place(tile, -9, -8, 0);
        assertEquals(barrel, tile.getVoxel(-9, -8, 0));
    }

    @Test
    public void testGetDefaultVoxel() {
        MCVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-32, -32, 0), new WorldCoords3d(32, 32, 255)));
        STONE.place(tile, -12, 12, 5);

        // Region created, chunk created with only one voxel
        assertEquals(MCVoxel.DEFAULT_VOXEL, tile.getVoxel(-12, 13, 98));
        // Region created, but no chunk
        assertEquals(MCVoxel.DEFAULT_VOXEL, tile.getVoxel(-17, 17, 39));
        // Returns something even if the region is not created
        assertEquals(MCVoxel.DEFAULT_VOXEL, tile.getVoxel(16, 16, 24));
    }
}
