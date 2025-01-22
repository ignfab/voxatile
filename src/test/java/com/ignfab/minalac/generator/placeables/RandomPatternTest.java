package com.ignfab.minalac.generator.placeables;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

public class RandomPatternTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new RandomPattern(new TestingPlaceable(), 0.5));
    }

    @Test
    void testPlacer() {
        // It is important to check that composite placeables pass seed (or salted seed) & model to sub-placeables.
        TestingPlaceable subPlaceable = new TestingPlaceable();
        TestingSeed seed = new TestingSeed("");
        Model model = new TestingModel();

        RandomPattern composite = new RandomPattern(subPlaceable, 0.5);

        assertDoesNotThrow(() -> composite.placer(seed, model));
        assertTrue(seed.isAncestorOfOrSameAs(subPlaceable.lastPlacer().seed()));
        assertEquals(model, subPlaceable.lastPlacer().model());
    }
}
