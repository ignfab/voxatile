package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dTest {
    @Test
    public void testConstructor() {
        WorldBBox3d box = assertDoesNotThrow(() -> new WorldBBox3d(0, 0, 0, 10, 10, 10), "Valid BBOX creation (10x10x10)");
        assertEquals(new WorldCoords3d(0, 0, 0), box.getMin(), "BBOX min");
        assertEquals(new WorldCoords3d(9, 9, 9), box.getMax(), "BBOX max");
        assertEquals(new WorldSize3d(10, 10, 10), box.getSize(), "BBOX size");

        assertThrows(IllegalArgumentException.class, () -> new WorldBBox3d(0, 0, 0, 0, 0, 0), "Invalid BBOX (size 0)");
        assertThrows(IllegalArgumentException.class, () -> new WorldBBox3d(0, 0, 0, -1, -1, -1), "Invalid BBOX (negative size)");
    }

    @Test
    @SuppressWarnings("checkstyle:ParenPad")
    public void testContains() {
        WorldBBox3d box = new WorldBBox3d(-1, -2, -3, 4, 5, 6);

        // Corners
        assertTrue(box.contains(-1, -2, -3), "BBOX contains corner (-1, -2, -3)");
        assertTrue(box.contains( 2, -2, -3), "BBOX contains corner ( 2, -2, -3)");
        assertTrue(box.contains(-1,  2, -3), "BBOX contains corner (-1,  2, -3)");
        assertTrue(box.contains( 2,  2, -3), "BBOX contains corner ( 2,  2, -3)");
        assertTrue(box.contains(-1, -2,  2), "BBOX contains corner (-1, -2,  2)");
        assertTrue(box.contains( 2, -2,  2), "BBOX contains corner ( 2, -2,  2)");
        assertTrue(box.contains(-1,  2,  2), "BBOX contains corner (-1,  2,  2)");
        assertTrue(box.contains( 2,  2,  2), "BBOX contains corner ( 2,  2,  2)");

        // Inside
        assertTrue(box.contains(0, -1, 1), "BBOX contains inside (0, -1, 1)");

        // Bordering
        assertFalse(box.contains(-2, 0, 0), "BBOX does not contain bordering (-2, 0, 0)");
        assertFalse(box.contains(0, -3, 0), "BBOX does not contain bordering (0, -3, 0)");
        assertFalse(box.contains(0, 0, -4), "BBOX does not contain bordering (0, 0, -4)");
        assertFalse(box.contains(3, 0, 0), "BBOX does not contain bordering (3, 0, 0)");
        assertFalse(box.contains(0, 3, 0), "BBOX does not contain bordering (0, 3, 0)");
        assertFalse(box.contains(0, 0, 3), "BBOX does not contain bordering (0, 0, 3)");

        // Outside
        assertFalse(box.contains(20, 10, 30), "BBOX does not contain outside (20, 10, 30)");
    }

    @Test
    public void testTo2d() {
        WorldBBox3d box = new WorldBBox3d(-1, -2, -3, 4, 5, 6);

        assertEquals(new WorldBBox2d(-1, -2, 4, 5), box.to2d(), "BBOX to 2d");
    }
}
