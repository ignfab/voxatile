package com.ignfab.minalac.generator.utils.random;

/**
 * A random number generator for map generation.
 * <p>
 * Difference with {@code java.util.Random} is that {@link #setSeed(long)} method
 * will salt internal seed with given seed instead of replacing it. This means base
 * seed will always have influence even after having used {@code setSeed}.
 */
public class Random extends java.util.Random {
    // This is the base seed
    private final long seed;

    private Random(long seed) {
        super(seed);
        // We use some more random here to help close seeds to generate very different sequences.
        this.seed = nextLong();
        setSeed(0);
    }

    /**
     * Creates a new {@code Random} generator from a given {@link Seed}.
     *
     * @param seed Seed for random generator
     */
    public Random(Seed seed) {
        this(seed.asLong());
    }

    // In this version, given seed will always be mixed to base seed before being used for random generation.
    @Override
    public void setSeed(long s) {
        super.setSeed(seed ^ s);
        // Ensures a good shuffle and divergeance between random sequences even for close sub-seeds.
        super.setSeed(nextLong());
    }

    /**
     * Sets the seed of this random number generator using two-dimensional coordinates seed.
     *
     * @param x Coordinate on x-axis
     * @param y Coordinate on y-axis
     */
    public void setSeed(int x, int y) {
        setSeed(x ^ (long) y << Integer.SIZE);
    }

    /**
     * Sets the seed of this random number generator using three dimensional coordinates seed.
     *
     * @param x Coordinate on x-axis
     * @param y Coordinate on y-axis
     * @param z Coordinate on z-axis
     */
    public void setSeed(int x, int y, int z) {
        setSeed(x ^ y << 8 ^ z << 16);
    }
}
