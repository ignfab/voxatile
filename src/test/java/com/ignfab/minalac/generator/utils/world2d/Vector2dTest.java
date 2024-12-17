package com.ignfab.minalac.generator.utils.world2d;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    @Test
    void testSquareLength() {
        assertEquals(0.0, new Vector2d(0.0, 0.0).squareLength());
        assertEquals(25.0, new Vector2d(-3.0, 4.0).squareLength());
    }

    @Test
    void testLength() {
        assertEquals(0.0, new Vector2d(0.0, 0.0).length());
        assertEquals(5.0, new Vector2d(3.0, -4.0).length());
    }

    @Test
    void testNormalized() {
        Vector2d vector = new Vector2d(-2.0, 3.0);
        double length = vector.length();
        Vector2d normalized = vector.normalized();

        assertEquals(1.0, normalized.length());
        assertEquals(-2.0, normalized.x() * length, 0.0001);
        assertEquals(3.0, normalized.y() * length, 0.0001);

        normalized = assertDoesNotThrow(() -> new Vector2d(0.0, 0.0).normalized());
        assertEquals(0.0, normalized.length());
    }

    @Test
    void component() {
        Vector2d vector = new Vector2d(-3.0, -4.0);
        assertEquals(-2.0, vector.component(-2.0, 14.0));

        assertEquals(Double.NaN, new Vector2d(0.0, 0.0).component(-2.0, 14.0));
    }
}


