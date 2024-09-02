package com.ignfab.minalac.generator.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IntegerIntervalTest {
    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new IntegerInterval(0, 2));
        assertDoesNotThrow(() -> new IntegerInterval(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new IntegerInterval(2, 0));
    }

    @Test
    void testCompareTo() {
        IntegerInterval interval = new IntegerInterval(1, 2);
        assertEquals(0, interval.compareTo(new IntegerInterval(1, 2)));
        assertTrue(interval.compareTo(new IntegerInterval(0, 2)) < 0);
        assertTrue(interval.compareTo(new IntegerInterval(1, 1)) < 0);
        assertTrue(interval.compareTo(new IntegerInterval(1, 3)) > 0);
        assertTrue(interval.compareTo(new IntegerInterval(2, 3)) > 0);
    }

    @Test
    void testOverlaps() {
        IntegerInterval interval = new IntegerInterval(1, 4);
        assertTrue(interval.overlaps(new IntegerInterval(-1, 1)));
        assertTrue(interval.overlaps(new IntegerInterval(2, 3)));
        assertTrue(interval.overlaps(new IntegerInterval(4, 6)));
        assertFalse(interval.overlaps(new IntegerInterval(-2, 0)));
        assertFalse(interval.overlaps(new IntegerInterval(5, 7)));
    }
}
