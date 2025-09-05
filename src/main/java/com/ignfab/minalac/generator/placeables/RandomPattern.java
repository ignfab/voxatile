package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.random.Random;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A pattern placing something or not according to a simple "dice roll".
 */
public class RandomPattern implements Pattern {
    private final Placeable placeable;
    private final double chance;
    private final Random random;

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param seed {@link Seed} to use for random number generation
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public RandomPattern(Seed seed, Placeable placeable, double chance) {
        this.placeable = placeable;
        this.chance = chance;
        this.random = seed.createRandom();
    }

    @Override
    public Placeable get(GenerationTile tile, int x, int y, int z) {
        random.setSeed(x, y, z);
        if (random.nextDouble() < chance)
            return placeable;
        return Nothing.INSTANCE;
    }
}
