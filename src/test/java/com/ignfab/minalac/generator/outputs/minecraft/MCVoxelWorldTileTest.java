package com.ignfab.minalac.generator.outputs.minecraft;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelWorldTileTest {

    @Test
    public void testIsOutOfLimits() {
        WorldBBox3d limits = new WorldBBox3d(new WorldCoords3d(-10, -20, 0), new WorldCoords3d(20, 30, 40));
        MCVoxelWorld world = new MCVoxelWorld();
        world.setLimits(limits);
        MCVoxelWorldTile tile = new MCVoxelWorldTile(world, limits);

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
}
