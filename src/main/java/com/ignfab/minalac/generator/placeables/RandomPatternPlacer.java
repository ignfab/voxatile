package com.ignfab.minalac.generator.placeables;

import java.util.Random;

import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Placer for a {@link RandomPattern}.
 */
public class RandomPatternPlacer implements Placer {

    private Random random;
    private Placer placer;
    private double chance;

    /**
     * Creates a new {@code RandomPatternPlacer}.
     *
     * @param seed Random seed to use (will be salted differently for each instance)
     * @param placer Placer to use to place stuff
     * @param chance Chance of stuff being placed (0.0 = never to 1.0 = always)
     */
    public RandomPatternPlacer(Seed seed, Placer placer, double chance) {
        this.random = seed.createRandom();
        this.placer = placer;
        this.chance = chance;
    }

    @Override
    public void place(int x, int y, int z) {
        if (Math.abs(random.nextDouble()) < chance)
            placer.place(x, y, z);
    }
}
