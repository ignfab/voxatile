package com.ignfab.minalac.generator.outputs.minetest;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MTVoxelTileTest {
    private static GenerationTile tile;
    private static MTVoxel grass;
    private static MTVoxel dirt;
    private static MTVoxel stone;

    private void initWorld(WorldBBox3d limits) {
        MTVoxelWorld world = assertDoesNotThrow(() -> new MTVoxelWorld(null));

        tile = assertDoesNotThrow(() -> new GenerationTile(
            new Generation(
                world,
                null,
                null,
                0.0, 0.0,
                limits.sizeX(), limits.sizeY(),
                1.0, 1.0,
                0, // Angle
                Math.max(Math.max(limits.sizeX(), limits.sizeY()), 16)
            ),
            limits
        ));

        assertDoesNotThrow(world::initialize);

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

        assertEquals(grass, tile.voxels().getVoxel(-1, -2, -3));
        assertEquals(stone, tile.voxels().getVoxel(0, -1, 1));
        assertEquals(dirt, tile.voxels().getVoxel(-1, 0, 0));

        initWorld(new WorldBBox3d(new WorldCoords3d(-5, -5, -20), new WorldCoords3d(50, 50, 500)));

        // Extremum of a map block [-16, -1]
        stone.place(tile, -2, -3, -1);
        grass.place(tile, -2, -3, -16);
        // Extremum of another map block [16, 31]
        dirt.place(tile, 37, 16, 387);
        stone.place(tile, 37, 31, 387);

        // Testing on MT max limits
        assertEquals(stone, tile.voxels().getVoxel(-2, -3, -1));
        assertEquals(grass, tile.voxels().getVoxel(-2, -3, -16));
        assertEquals(dirt, tile.voxels().getVoxel(37, 16, 387));
        assertEquals(stone, tile.voxels().getVoxel(37, 31, 387));
    }
}
