package com.ignfab.minalac.generator.utils.random;

import java.util.Random;

/**
 * A seed for a sequence of random numbers.
 *
 * See docs/development/RandomNumbers.md for further documentation.
 */
public class Seed {
    private final String seed;

    /**
     * Creates a new {@code Seed} from a String.
     *
     * @param seed the seed as a String
     */
    public Seed(String seed) {
        this.seed = seed;
    }

    /**
     * Creates a new {@code Seed} object resulting of seed salted with salt.
     *
     * @param salt the salt to use for salting seed
     *
     * @return the salted seed
     */
    public Seed salt(String salt) {
        return new Seed(seed + salt);
    }

   /**
     * Creates a new {@code Seed} object resulting of seed salted with salting object.
     *
     * @param salting the salting object to use for salting seed
     *
     * @return the salted seed
     */
    public Seed salt(Salting salting) {
        return salt(salting.salt());
    }

    /**
     * Creates a {@code Random} object from this seed.
     * All calls on the same seed will give a {@code Random} object generating
     * the same sequence of numbers.
     *
     * @return a new {@code Random} object
     */
    public Random createRandom() {
        // We could use hashCode() but it's only int and random uses long seeds
        long s = 0;
        for (char c : seed.toCharArray())
            s = 31L * s + c;

        return new Random(s);
    }

    /**
     * Creates a {@code Random} object from this seed with salt.
     * This is a shortcut to {@code .salt(salt).createRandom()}.
     *
     * @param salt the salt to use for salting seed
     *
     * @return a new {@code Random} object
     */
    public Random createRandom(String salt) {
        return salt(salt).createRandom();
    }


    /**
     * Creates a {@code Random} object from this seed with salt.
     * This is a shortcut to {@code .salt(salting).createRandom()}.
     *
     * @param salting the salting object to use for salting seed
     *
     * @return a new {@code Random} object
     */
    public Random createRandom(Salting salting) {
        return salt(salting).createRandom();
    }
}
