package com.ignfab.minalac.generator.utils.world2d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.Vector3d;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    static final double EPSILON = 0.0000001;

    @Test
    public void testZero() {
        assertEquals(new Vector2d(0.0, 0.0), Vector2d.ZERO);
    }

    @Test
    public void testStaticLength() {
        assertEquals(0.0, Vector2d.length(0.0, 0.0), EPSILON);
        assertEquals(Math.sqrt(2.0), Vector2d.length(1.0, 1.0));
        assertEquals(5.0, Vector2d.length(3.0, 4.0), EPSILON);
    }

    @Test
    public void testLength() {
        assertEquals(0.0, new Vector2d(0.0, 0.0).length(), EPSILON);
        assertEquals(Math.sqrt(2.0), new Vector2d(1.0, 1.0).length());
        assertEquals(5.0, new Vector2d(3.0, 4.0).length(), EPSILON);
        assertEquals(0.0, Vector3d.ZERO.length(), EPSILON);
    }

    @Test
    public void testIsZero() {
        assertTrue(new Vector2d(0.0, 0.0).isZero());
        assertFalse(new Vector2d(1.0, 0.0).isZero());
        assertFalse(new Vector2d(0.0, 1.0).isZero());
        assertTrue(Vector2d.ZERO.isZero());
    }

    @Test
    public void testAdd() {
        assertEquals(new Vector2d(5.0, -3.0), new Vector2d(1.0, 2.0).add(new Vector2d(4.0, -5.0)));
    }

    @Test
    public void testSubtract() {
        assertEquals(new Vector2d(-3.0, 7.0), new Vector2d(1.0, 2.0).subtract(new Vector2d(4.0, -5.0)));
    }

    @Test
    public void testMultiply() {
        assertEquals(new Vector2d(4.0, -6.0), new Vector2d(2.0, -3.0).multiply(2.0));
        assertEquals(new Vector2d(-6.0, 9.0), new Vector2d(2.0, -3.0).multiply(-3.0));
        assertEquals(Vector2d.ZERO, Vector2d.ZERO.multiply(2.0));
    }

    @Test
    public void testOpposite() {
        assertEquals(new Vector2d(-3.0, 4.0), new Vector2d(3.0, -4.0).opposite());
        assertEquals(Vector2d.ZERO, Vector2d.ZERO.opposite());
    }

    @Test
    public void testRound() {
        assertEquals(new WorldCoords2d(0, 0), new Vector2d(0.2, -0.3).round());
        assertEquals(new WorldCoords2d(1, -1), new Vector2d(0.6, -0.7).round());
    }

    @Test
    public void testTo3d() {
        assertEquals(new Vector3d(2.0, 3.0, 4.0), new Vector2d(2.0, 3.0).to3d(4.0));
        assertEquals(Vector3d.ZERO, Vector2d.ZERO.to3d(0.0));
    }

    @Test
    public void testNormal() {
        assertEquals(new Vector2d(2.0, -1.0), new Vector2d(1.0, 2.0).normal());
        assertEquals(Vector2d.ZERO, Vector2d.ZERO.normal());
    }

    @Test
    public void testDeterminant() {
        Vector2d v = new Vector2d(2.0, 3.0);

        assertEquals(0.0, v.determinant(v), EPSILON);
        assertEquals(v.length() * v.length(), Math.abs(v.determinant(v.normal())), EPSILON);
        assertEquals(0.0, v.determinant(Vector2d.ZERO), EPSILON);
    }

    @Test
    public void testScalarProduct() {
        Vector2d v = new Vector2d(2.0, 3.0);
        assertEquals(v.length() * v.length(), v.dot(v), EPSILON);
        assertEquals(0.0, v.dot(v.normal()), EPSILON);
        assertEquals(0.0, v.dot(Vector2d.ZERO), EPSILON);
    }

    @Test
    public void testEquals() {
        assertTrue(new Vector2d(-3, 4).equals(new Vector2d(-3, 4)));
        assertFalse(new Vector2d(-2, -4).equals(new Vector2d(-3, -4)));
        assertFalse(new Vector2d(3, 5).equals(new Vector2d(3, 4)));
        assertTrue(new Vector2d(0, 0).equals(Vector2d.ZERO));
    }
}
