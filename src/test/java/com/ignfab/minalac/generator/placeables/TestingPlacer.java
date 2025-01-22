package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A {@code Placer} for testing purposes.
 */
public class TestingPlacer implements Placer {

    private final Model model;
    private final Seed seed;
    private WorldCoords3d lastPlaced = null;

    /**
     * Creates a new {@code TestingPlacer}. Supposed to be used only in {@code TestingPlaceable.placer()}
     *
     * @param seed Random seed
     * @param model Model
     */
    TestingPlacer(Seed seed, Model model) {
        this.seed = seed;
        this.model = model;
    }

    /**
     * Returns internal seed for checking.
     *
     * @return seed
     */
    Seed seed() {
        return seed;
    }

    /**
     * Returns internal model for checking.
     *
     * @return model
     */
    Model model() {
        return model;
    }

    WorldCoords3d lastPlaced() {
        return lastPlaced;
    }

    @Override
    public void place(int x, int y, int z) {
        lastPlaced = new WorldCoords3d(x, y, z);
    }

}
