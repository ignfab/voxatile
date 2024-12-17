package com.ignfab.minalac.generator.utils.world3d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

public class Vector3dTest {
    @Test
    void testSquareLength() {
        assertEquals(0.0, new Vector3d(0.0, 0.0, 0.0).squareLength());
        assertEquals(169.0, new Vector3d(-3.0, -4.0, 12.0).squareLength());
    }

    @Test
    void testLength() {
        assertEquals(0.0, new Vector3d(0.0, 0.0, 0.0).length());
        assertEquals(13.0, new Vector3d(-3.0, -4.0, 12.0).length());
    }

    @Test
    void testNormalized() {
        Vector3d vector = new Vector3d(-1.0, 2.0, 3.0);
        double length = vector.length();
        Vector3d normalized = assertDoesNotThrow(vector::normalized);

        assertEquals(1.0, normalized.length());
        assertEquals(-1.0, normalized.x() * length, 0.0001);
        assertEquals(2.0, normalized.y() * length, 0.0001);
        assertEquals(3.0, normalized.z() * length, 0.0001);

        normalized = assertDoesNotThrow(() -> new Vector3d(0.0, 0.0, 0.0).normalized());
        assertEquals(0.0, normalized.length());
    }

    @Test
    void componentXY() {
        Vector3d vector = new Vector3d(3.0, -4.0, -5.0);
        assertEquals(1.0, vector.componentXY(6.0, -8.0));

        assertEquals(Double.NaN, new Vector3d(0.0, 0.0, 0.0).componentXY(6.0, -8.0));
    }

    @Test
    void testToXY() {
        Vector2d vector = assertDoesNotThrow(() -> new Vector3d(-4.0, 6.0, -8.0).toXY());
        assertEquals(-4.0, vector.x(), 0.0001);
        assertEquals(6.0, vector.y(), 0.0001);
    }
}
