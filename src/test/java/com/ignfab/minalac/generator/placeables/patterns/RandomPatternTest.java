package com.ignfab.minalac.generator.placeables.patterns;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.TestingPlaceable;
import com.ignfab.minalac.generator.utils.random.TestingRandom;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.TestingVoxelTile;

import static org.junit.jupiter.api.Assertions.*;

public class RandomPatternTest {
    @Test
    public void testConstructor() {
        Placeable placeable = new TestingPlaceable();

        assertDoesNotThrow(() -> new RandomPattern(new TestingSeed(""), placeable, 0.5));
    }

    @Test
    public void testPlace() {
        TestingSeed seed = new TestingSeed("not empty");
        TestingRandom random = seed.random();
        TestingPlaceable placeable = new TestingPlaceable();

        RandomPattern pattern1 = new RandomPattern(seed, placeable, 0.5);
        random.setNextDouble(1.0);
        assertDoesNotThrow(() -> pattern1.place(TestingVoxelTile.UNUSED, 0, 0, 0));
        assertNull(placeable.lastPlaced());

        random.setNextDouble(0.0);
        RandomPattern pattern2 = new RandomPattern(seed, placeable, 0.5);
        assertDoesNotThrow(() -> pattern2.place(TestingVoxelTile.UNUSED, 0, 0, 0));
        assertEquals(new WorldCoords3d(0, 0, 0), placeable.lastPlaced());
    }
}
