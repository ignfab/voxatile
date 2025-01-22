package com.ignfab.minalac.generator.placeables;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.random.TestingSeed;

public class CombinedPlaceableTest {
    @Test
    void testConstructor() {
        assertDoesNotThrow(VoxelStructure::new);
    }

    @Test
    void testPlacer() {
        // It is important to check that composite placeables pass seed (or salted seed) & model to sub-placeables.
        TestingPlaceable subPlaceable = new TestingPlaceable();
        TestingSeed seed = new TestingSeed("");
        Model model = new TestingModel();

        CombinedPlaceable composite = new CombinedPlaceable();
        composite.add(subPlaceable);

        assertDoesNotThrow(() ->  composite.placer(seed, model));
        assertTrue(seed.isAncestorOfOrSameAs(subPlaceable.lastPlacer().seed()));
        assertEquals(model, subPlaceable.lastPlacer().model());
    }
}
