package com.ignfab.minalac.generator.utils.random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TestingSeedTest {

    @Test
    public void testIsAncestorOfOrSameAs() {
        TestingSeed seed = new TestingSeed("a");
        Seed seed0 = seed;
        Seed seed1 = seed0.salt("b");
        Seed seed2 = seed1.salt("c");
        Seed seed3 = new TestingSeed("d");

        assertTrue(seed.isAncestorOfOrSameAs(seed0)); // Same seed
        assertTrue(seed.isAncestorOfOrSameAs(seed1)); // Child seed
        assertTrue(seed.isAncestorOfOrSameAs(seed2)); // Grand child seed
        assertFalse(seed.isAncestorOfOrSameAs(seed3)); // Alien seed
    }

    @Test
    public void testIsDescendantOf() {
        TestingSeed seed = new TestingSeed("a");
        TestingSeed seed1 = seed.salt("b");
        TestingSeed seed2 = seed1.salt("c");
        TestingSeed seed3 = new TestingSeed("d");

        assertFalse(seed.isDescendantOf(seed)); // Same seed
        assertTrue(seed1.isDescendantOf(seed)); // Parent seed
        assertTrue(seed2.isDescendantOf(seed)); // Grand parent seed
        assertFalse(seed3.isDescendantOf(seed)); // Alien seed
    }
}
