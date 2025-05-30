package com.ignfab.minalac.generator.utils.random;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomTest {

    // Number of random numbers to test in sequences
    static final int NUMBER = 10;

    private static void assertArrayNotEquals(int[] expected, int[] actual) {
        assertFalse(Arrays.equals(expected, actual));
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new Random(TestingSeed.UNUSED));
    }

    @Test
    public void testSeed() {
        Random randomA = new Random(new Seed("A"));
        Random randomB = new Random(new Seed("B"));

        int[] a = new int[NUMBER];
        int[] b = new int[NUMBER];

        for (int n = 0; n < NUMBER; n++) {
            a[n] = randomA.nextInt();
            b[n] = randomB.nextInt();
        }
        assertArrayNotEquals(a, b);
    }

    @Test
    public void testSetSeedLong() {
        Random randomA = new Random(new Seed("A"));
        Random randomB = new Random(new Seed("B"));

        randomA.setSeed(1);
        randomB.setSeed(1);

        int[] a = new int[NUMBER];
        int[] b = new int[NUMBER];
        for (int n = 0; n < NUMBER; n++) {
            a[n] = randomA.nextInt();
            b[n] = randomB.nextInt();
        }
        assertArrayNotEquals(a, b);

        randomA.setSeed(2);
        int[] c = new int[NUMBER];
        for (int n = 0; n < NUMBER; n++)
            c[n] = randomA.nextInt();
        assertArrayNotEquals(a, c);

        randomA.setSeed(1);
        int[] d = new int[NUMBER];
        for (int n = 0; n < NUMBER; n++)
            d[n] = randomA.nextInt();
        assertArrayEquals(a, d);
    }
}
