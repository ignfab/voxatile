package com.ignfab.minalac.generator.utils.random;

/**
 * An object capable of salting a {@link Seed}.
 */
public interface Salting {
    /**
     * Returns an object dependant salt.
     * <p>
     * Salt string should be enough unique to this object so two objects
     * may not share the same randomness.
     *
     * @return salt
     */
    String salt();
}
