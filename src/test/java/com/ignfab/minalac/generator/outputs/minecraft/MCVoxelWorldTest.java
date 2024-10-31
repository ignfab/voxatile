package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import net.querz.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelWorldTest {
    @TempDir
    private File dir;

    @Test
    public void testSave() {
        MCVoxelWorld world = new MCVoxelWorld();
        world.setLimits(new WorldBBox3d(
            new WorldCoords3d(-16, -16, 0),
            new WorldCoords3d(15, 15, 255)));
        world.getMetadata().setWorldName("testSave");
        world.getMetadata().setSpawn(new WorldCoords3d(0, 64, 0));
        CompoundTag block = new CompoundTag();
        block.putString("Name", "minecraft:stone");

        world.setBlockState(0, 64, 0, block); // Region (0, 0)
        world.setBlockState(0, 64, -1, block); // Region (0, -1)

        assertDoesNotThrow(() -> world.save(dir));
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

    @Test
    public void testIsOutOfLimits() {
        MCVoxelWorld world = new MCVoxelWorld();
        world.setLimits(new WorldBBox3d(new WorldCoords3d(-10, -20, 0), new WorldCoords3d(20, 30, 40)));

        // X/Y/Z => X/Z/-Y
        assertFalse(world.isOutOfLimits(-10, 0, 19));
        assertFalse(world.isOutOfLimits(20, 40, -31));

        assertFalse(world.isOutOfLimits(5, 5, -21));

        assertTrue(world.isOutOfLimits(21, 40, -31));
        assertTrue(world.isOutOfLimits(20, 41, -31));
        assertTrue(world.isOutOfLimits(20, 40, -32));
        assertTrue(world.isOutOfLimits(-11, 0, 19));
        assertTrue(world.isOutOfLimits(-10, -1, 19));
        assertTrue(world.isOutOfLimits(-10, 0, 20));
    }
}
