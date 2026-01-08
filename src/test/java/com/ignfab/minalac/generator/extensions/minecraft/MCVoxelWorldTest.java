package com.ignfab.minalac.generator.extensions.minecraft;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelWorldTest {
    @TempDir
    private File dir;

    @Test
    public void testSave() {
        WorldBBox3d limits = new WorldBBox3d(
            new WorldCoords3d(-16, -16, 0),
            new WorldCoords3d(15, 15, 255));
        MCVoxelWorld world = assertDoesNotThrow(() -> new MCVoxelWorld(dir));
        world.setLimits(limits);
        assertDoesNotThrow(world::initialize);

        MCVoxelTile tile = world.newTile(limits);

        world.getMetadata().setWorldName("testSave");
        world.getMetadata().setSpawn(new WorldCoords3d(0, 64, 0));
        CompoundTag block = new CompoundTag();
        block.putString("Name", "minecraft:stone");


        tile.setBlockState(0, 64, 0, block); // Region (0, 0)
        tile.setBlockState(0, 64, -1, block); // Region (0, -1)

        assertDoesNotThrow(() -> tile.save());
        assertDoesNotThrow(() -> world.finalizeAndSave());
        File[] files = dir.listFiles();
        assertNotNull(files);
        assertEquals(2, files.length);

        for (File file : files) {
            switch (file.getName()) {
                case "level.dat" -> assertTrue(file.exists());
                case "region" -> {
                    assertTrue(file.isDirectory());
                    File[] regions = file.listFiles();
                    assertNotNull(regions);
                    assertEquals(2, regions.length);
                    List<String> expected = Arrays.asList("r.0.0.mca", "r.0.-1.mca");
                    List<String> actual = Arrays.stream(regions).map(File::getName).toList();
                    assertTrue(expected.size() == actual.size() && expected.containsAll(actual) && actual.containsAll(expected));
                }
                default -> fail("Unexpected file: " + file.getName());
            }
        }
    }
}
