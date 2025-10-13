package com.ignfab.minalac.generator.modules.minecraft;

import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftVoxelTileTest {
    private static final MinecraftVoxel STONE = new MinecraftVoxel("minecraft:stone");

    private MinecraftVoxelTile initTile(WorldBBox3d limits) {
        MinecraftVoxelWorld world = assertDoesNotThrow(() -> new MinecraftVoxelWorld(null));
        world.setLimits(limits);
        assertDoesNotThrow(world::initialize);
        return world.newTile(limits);
    }

    @Test
    public void testIsOutOfLimits() {
        MinecraftVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-10, -20, 0), new WorldCoords3d(20, 30, 40)));

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
    public void testGetVoxel() {
        MinecraftVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-20, -20, 0), new WorldCoords3d(50, 50, 255)));
        MinecraftVoxel barrel = new MinecraftVoxel("minecraft:barrel", Map.of(
            "facing", "south",
            "open", "true"
        ));
        MinecraftVoxel dirt = new MinecraftVoxel("minecraft:dirt");
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
    public void testGetBlockEntityVoxel() {
        MinecraftVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-20, -20, 0), new WorldCoords3d(50, 50, 255)));
        CompoundTag data = new CompoundTag();
        data.putInt("OutputSignal", 7);
        MinecraftBlockEntityVoxel comparator = new MinecraftBlockEntityVoxel("minecraft:comparator", "minecraft:comparator", Map.of(
            "facing", "south",
            "mode", "compare",
            "powered", "true"
        ), null);
        MinecraftBlockEntityVoxel daylightDetector = new MinecraftBlockEntityVoxel("minecraft:daylight_detector", "minecraft:daylight_detector", null, null);
        comparator.place(tile, -9, -8, 1);
        daylightDetector.place(tile, -7, 5, 55);

        assertEquals(comparator, tile.getVoxel(-9, -8, 1));
        assertEquals(daylightDetector, tile.getVoxel(-7, 5, 55));
    }

    @Test
    public void testGetDefaultVoxel() {
        MinecraftVoxelTile tile = initTile(new WorldBBox3d(new WorldCoords3d(-32, -32, 0), new WorldCoords3d(32, 32, 255)));
        STONE.place(tile, -12, 12, 5);

        // Region created, chunk created with only one voxel
        assertEquals(MinecraftVoxel.DEFAULT_VOXEL, tile.getVoxel(-12, 13, 98));
        // Region created, but no chunk
        assertEquals(MinecraftVoxel.DEFAULT_VOXEL, tile.getVoxel(-17, 17, 39));
        // Returns something even if the region is not created
        assertEquals(MinecraftVoxel.DEFAULT_VOXEL, tile.getVoxel(16, 16, 24));
    }
}
