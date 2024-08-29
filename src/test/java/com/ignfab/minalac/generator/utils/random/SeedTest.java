package com.ignfab.minalac.generator.utils.random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

public class SeedTest {

    // Number of random numbers to test in sequences
    static final int NUMBER = 10;

    private static void assertArrayNotEquals(int[] expecteds, int[] actuals) {
        assertFalse(Arrays.equals(expecteds, actuals));
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> {
            new Seed("1234");
        });
        assertDoesNotThrow(() -> {
            new Seed("");
        });
    }

    @Test
    public void testCreateRandom() {
        Seed baseSeed = new Seed("abcdef");
        Seed sameSeed = new Seed("abcdef");
        Seed differentSeed = new Seed("abcdeg");

        Random baseRandom = baseSeed.createRandom();
        Random secondBaseRandom = baseSeed.createRandom();
        Random sameRandom = sameSeed.createRandom();
        Random differentRandom = differentSeed.createRandom();

        int[] base = new int[NUMBER];
        int[] second = new int[NUMBER];
        int[] same = new int[NUMBER];
        int[] different = new int[NUMBER];

        for (int n = 0; n < NUMBER; n++) {
            base[n] = baseRandom.nextInt();
            second[n] = secondBaseRandom.nextInt();
            same[n] = sameRandom.nextInt();
            different[n] = differentRandom.nextInt();
        }

        assertArrayEquals(base, second);
        assertArrayEquals(base, same);
        assertArrayNotEquals(base, different);
    }

    @Test
    public void testSalt() {
        Seed baseSeed = new Seed("abcdef");
        Seed saltedSeed = baseSeed.salt("pepper");
        Seed sameSaltedSeed = baseSeed.salt("pepper");
        Seed differentSaltedSeed = baseSeed.salt("garlic");

        Random baseRandom = baseSeed.createRandom();
        Random saltedRandom = saltedSeed.createRandom();
        Random sameRandom = sameSaltedSeed.createRandom();
        Random differentRandom = differentSaltedSeed.createRandom();

        int[] base = new int[NUMBER];
        int[] salted = new int[NUMBER];
        int[] same = new int[NUMBER];
        int[] different = new int[NUMBER];

        for (int n = 0; n < NUMBER; n++) {
            base[n] = baseRandom.nextInt();
            salted[n] = saltedRandom.nextInt();
            same[n] = sameRandom.nextInt();
            different[n] = differentRandom.nextInt();
        }

        assertArrayNotEquals(base, salted);
        assertArrayEquals(salted, same);
        assertArrayNotEquals(salted, different);
    }
}
