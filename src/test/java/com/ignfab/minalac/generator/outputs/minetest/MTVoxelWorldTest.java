package com.ignfab.minalac.generator.outputs.minetest;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.VoxelType;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MTVoxelWorldTest {
    private static MTVoxelWorld world;
    private static VoxelType grass;
    private static VoxelType dirt;
    private static VoxelType stone;

    private void initWorld(WorldBBox3d limits) {
        world = new MTVoxelWorld();
        world.setLimits(limits);
        grass = new MTVoxelType(world, "default:dirt_with_grass", (byte) 0, (byte) 0);
        dirt = new MTVoxelType(world, "default:dirt", (byte) 0, (byte) 0);
        stone = new MTVoxelType(world, "default:stone", (byte) 0, (byte) 0);
    }


    @Test
    public void testGetVoxel() {
        initWorld(new WorldBBox3d(new WorldCoords3d(-1, -2, -5), new WorldCoords3d(2, 3, 6)));
        grass.place(-1, -2, -3);
        stone.place(0, -1, 1);
        dirt.place(-1, 0, 0);

        assertEquals(grass, world.getVoxel(-1, -2, -3));
        assertEquals(stone, world.getVoxel(0, -1, 1));
        assertEquals(dirt, world.getVoxel(-1, 0, 0));

        initWorld(new WorldBBox3d(new WorldCoords3d(-5, -5, -20), new WorldCoords3d(50, 50, 500)));

        // Extremum of a map block [-16, -1]
        stone.place(-2, -3, -1);
        grass.place(-2, -3, -16);
        // Extremum of another map block [16, 31]
        dirt.place(37, 16, 387);
        stone.place(37, 31, 387);

        // Testing on MT max limits
        assertEquals(stone, world.getVoxel(-2, -3, -1));
        assertEquals(grass, world.getVoxel(-2, -3, -16));
        assertEquals(dirt, world.getVoxel(37, 16, 387));
        assertEquals(stone, world.getVoxel(37, 31, 387));
    }
}
