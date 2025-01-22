package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;

public class TestingPlaceable implements Placeable {

    private TestingPlacer lastPlacer;

    @Override
    public Placer placer(Seed seed, Model model) {
        lastPlacer = new TestingPlacer(seed, model);
        return lastPlacer;
    }

    public TestingPlacer lastPlacer() {
        return lastPlacer;
    }
}
