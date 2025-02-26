package com.ignfab.minalac.generator.placeables;

import java.util.Random;

/**
 * A pattern placing something or not according to a simple "dice roll".
 */
public class RandomPattern implements Pattern {
    private final Placeable placeable;
    private final double chance;

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public RandomPattern(Placeable placeable, double chance) {
        this.placeable = placeable;
        this.chance = chance;
    }

    @Override
    public Placeable get(Random random, int x, int y, int z) {
        if (Math.abs(random.nextDouble()) < chance)
            return placeable;
        return NoVoxel.INSTANCE;
    }
}
