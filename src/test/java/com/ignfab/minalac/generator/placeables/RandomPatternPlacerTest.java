package com.ignfab.minalac.generator.placeables;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.random.TestingSeed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class RandomPatternPlacerTest {
    @Test
    public void testConstructor() {
        TestingSeed seed = new TestingSeed("");
        Placer placer = new TestingPlaceable().placer(seed, new TestingModel());

        assertDoesNotThrow(() -> new RandomPatternPlacer(seed, placer, 0.5));
    }

    @Test
    public void testPlace() {
        TestingSeed seed = new TestingSeed("");
        TestingPlaceable placeable = new TestingPlaceable();

        seed.random().setNextDouble(1.0);
        RandomPattern pattern1 = new RandomPattern(placeable, 0.5);
        Placer placer1 = pattern1.placer(seed, null);
        assertDoesNotThrow(() -> placer1.place(0, 0, 0));
        assertNull(placeable.lastPlacer().lastPlaced());

        seed.random().setNextDouble(0.0);
        RandomPattern pattern2 = new RandomPattern(placeable, 0.5);
        Placer placer2 = pattern2.placer(seed, null);
        assertDoesNotThrow(() -> placer2.place(0, 0, 0));
        assertEquals(new WorldCoords3d(0, 0, 0), placeable.lastPlacer().lastPlaced());
    }
}
