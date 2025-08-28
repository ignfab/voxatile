package com.ignfab.minalac.generator.utils.world3d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

public class Vector3dTest {
    static final double EPSILON = 0.0000001;

    @Test
    public void testZero() {
        assertEquals(new Vector3d(0.0, 0.0, 0.0), Vector3d.ZERO);
    }

    @Test
    public void testStaticLength() {
        assertEquals(0.0, Vector3d.length(0.0, 0.0, 0.0), EPSILON);
        assertEquals(Math.sqrt(3.0), Vector3d.length(1.0, 1.0, 1.0), EPSILON);
        assertEquals(7.0, Vector3d.length(2.0, -3.0, 6.0), EPSILON);
    }

    @Test
    public void testLength() {
        assertEquals(0.0, new Vector3d(0.0, 0.0, 0.0).length(), EPSILON);
        assertEquals(Math.sqrt(3.0), new Vector3d(1.0, 1.0, 1.0).length(), EPSILON);
        assertEquals(7.0, new Vector3d(2.0, -3.0, 6.0).length(), EPSILON);
        assertEquals(0.0, Vector3d.ZERO.length(), EPSILON);
    }

    @Test
    public void testIsZero() {
        assertTrue(new Vector3d(0.0, 0.0, 0.0).isZero());
        assertFalse(new Vector3d(1.0, 0.0, 0.0).isZero());
        assertFalse(new Vector3d(0.0, 1.0, 0.0).isZero());
        assertFalse(new Vector3d(0.0, 0.0, 1.0).isZero());
        assertTrue(Vector3d.ZERO.isZero());
    }

    @Test
    public void testAdd() {
        assertEquals(new Vector3d(5.0, -3.0, 1.0), new Vector3d(1.0, 2.0, 3.0).add(new Vector3d(4.0, -5.0, -2.0)));
    }

    @Test
    public void testSubtract() {
        assertEquals(new Vector3d(-3.0, 7.0, 5.0), new Vector3d(1.0, 2.0, 3.0).subtract(new Vector3d(4.0, -5.0, -2.0)));
    }

    @Test
    public void testMultiply() {
        assertEquals(new Vector3d(4.0, -6.0, 8.0), new Vector3d(2.0, -3.0, 4.0).multiply(2.0));
        assertEquals(new Vector3d(-6.0, 9.0, -12.0), new Vector3d(2.0, -3.0, 4.0).multiply(-3.0));
        assertEquals(Vector3d.ZERO, Vector3d.ZERO.multiply(2.0));
    }

    @Test
    public void testOpposite() {
        assertEquals(new Vector3d(-3.0, 4.0, -5.0), new Vector3d(3.0, -4.0, 5.0).opposite());
        assertEquals(Vector3d.ZERO, Vector3d.ZERO.opposite());
    }

    @Test
    public void testRound() {
        assertEquals(new WorldCoords3d(0, 0, 0), new Vector3d(0.2, -0.3, 0.4).round());
        assertEquals(new WorldCoords3d(1, -1, 1), new Vector3d(0.6, -0.7, 0.9).round());
    }

    @Test
    public void testTo2d() {
        assertEquals(new Vector2d(2.0, 3.0), new Vector3d(2.0, 3.0, 4.0).to2d());
        assertEquals(Vector2d.ZERO, Vector3d.ZERO.to2d());
    }

    @Test
    public void testEquals() {
        assertTrue(new Vector3d(-3, 4, 5).equals(new Vector3d(-3, 4, 5)));
        assertFalse(new Vector3d(-2, -4, 5).equals(new Vector3d(-3, -4, 5)));
        assertFalse(new Vector3d(3, 5, -5).equals(new Vector3d(3, 4, -5)));
        assertFalse(new Vector3d(-3, 4, 5).equals(new Vector3d(-3, 4, -5)));
        assertTrue(new Vector3d(0, 0, 0).equals(Vector3d.ZERO));
    }
}
