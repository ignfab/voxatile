package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.OutOfWorldException;
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
    public void testSave() throws OutOfWorldException {
        MCVoxelWorld world = new MCVoxelWorld();
        world.getMetadata().setWorldName("testSave");
        world.getMetadata().setSpawn(new WorldCoords3d(0, 64, 0));
        world.getMetadata().setBbox(new WorldBBox3d(-16, 0, -16, 32, 255, 32));
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
    @SuppressWarnings("checkstyle:ParenPad")
    public void testCheckLimits() {
        MCVoxelWorld world = new MCVoxelWorld();

        assertDoesNotThrow(() -> world.checkLimits(-30_000_000, 0, -30_000_000));
        assertDoesNotThrow(() -> world.checkLimits(30_000_000, 255, 30_000_000));

        assertDoesNotThrow(() -> world.checkLimits(0, 64, 0));

        assertThrows(OutOfWorldException.class, () -> world.checkLimits(-30_000_001, 64,           0));
        assertThrows(OutOfWorldException.class, () -> world.checkLimits(          0, -1,           0));
        assertThrows(OutOfWorldException.class, () -> world.checkLimits(          0, 64, -30_000_001));
        assertThrows(OutOfWorldException.class, () -> world.checkLimits(30_000_001,  64,          0));
        assertThrows(OutOfWorldException.class, () -> world.checkLimits(         0, 256,          0));
        assertThrows(OutOfWorldException.class, () -> world.checkLimits(         0,  64, 30_000_001));
    }
}
