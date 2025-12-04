package com.ignfab.minalac.generator.placeables.patterns;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.PerlinNoise;
import com.ignfab.minalac.generator.utils.random.Random;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * A pattern placing something or not according to a perlin noise.
 */
public class PerlinPattern implements Pattern {
    private final Placeable placeable;
    private final double chance;
    private final Random random;
    private final double gridCellSize;
    private final boolean ignoreZ;

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param seed {@link Seed} to use for random number generation
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     * @param gridCellSize Size of a grid cell (should be positive and below 1 to produce useful results)
     * @param ignoreZ Whether to compute 2d or 3d perlin noise
     */
    public PerlinPattern(Seed seed, Placeable placeable, double chance, double gridCellSize, boolean ignoreZ) {
        this.placeable = placeable;
        this.chance = chance;
        this.random = seed.createRandom();
        this.gridCellSize = gridCellSize;
        this.ignoreZ = ignoreZ;
    }

    @Override
    public Placeable get(int x, int y, int z) {
        double perlin = ignoreZ ? PerlinNoise.get2d(random, x, y, gridCellSize) : PerlinNoise.get3d(random, x, y, z, gridCellSize);
        return perlin < chance ? placeable : Nothing.INSTANCE;
    }
}
