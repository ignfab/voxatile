package com.ignfab.minalac.generator.utils.random;

/**
 * A testing seed class derived from Seed but with some mock and probe methods.
 */
public class TestingSeed extends Seed {

    private final TestingSeed parent;
    private final TestingRandom random;

    private TestingSeed(String seed, TestingSeed parent) {
        super(seed);
        this.parent = parent;
        // Keep same parent random object if any, for testing purposes
        if (parent != null)
            random = parent.random();
        else
            random = new TestingRandom();
    }

    /**
     * Creates a new {@code TestingSeed} from a String.
     *
     * @param seed the seed as a String
     */
    public TestingSeed(String seed) {
        this(seed, null);
    }

    /**
     * Tells if seed is ancestor of or same as given seed.
     *
     * @param seed seed to test
     *
     * @return True if seed is ancestor of or same as given seed
     */
    public boolean isAncestorOfOrSameAs(Seed seed) {
        if (this == seed) return true;
        if (seed instanceof TestingSeed testingSeed)
            return testingSeed.isDescendantOf(this);
        return false;
    }

    /**
     * Tells if seed descends from given testing seed.
     *
     * @param seed testing seed to test
     *
     * @return True if seed is desendant of (and not same as) given seed
     */
    public boolean isDescendantOf(Seed seed) {
        return (parent != null) && (parent == seed || parent.isDescendantOf(seed));
    }

    /**
     * Creates a new {@code TestingSeed} object resulting of seed salted with salt.
     *
     * @param salt the salt to use for salting seed
     *
     * @return the salted seed
     */
    public TestingSeed salt(String salt) {
        return new TestingSeed(seed + salt, this);
    }

   /**
     * Creates a new {@code TestingSeed} object resulting of seed salted with salting object.
     *
     * @param salting the salting object to use for salting seed
     *
     * @return the salted seed
     */
    public Seed salt(Salting salting) {
        return salt(salting.salt());
    }

    /**
     * Fakes a {@code Random} object creation. Actually, this is an alias for {@code random()}
     *
     * @return a {@code TestingRandom} object
     */
    public TestingRandom createRandom() {
        return random();
    }


    /**
     * Returns fake {@code Random} object returned by {@code createRandom()} method.
     *
     * @return a {@code TestingRandom} object
     */
    public TestingRandom random() {
        return random;
    }
}
