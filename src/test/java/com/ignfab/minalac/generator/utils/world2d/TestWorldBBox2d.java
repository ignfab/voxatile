package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestWorldBBox2d {
    @Test
    public void testConstructor() {
        // Instantiating a new voxel box
        WorldBBox2d box = assertDoesNotThrow(() -> new WorldBBox2d(0, 0, 10, 10));
        assertEquals(new WorldCoords2d(0, 0), box.getMin());
        assertEquals(new WorldCoords2d(9, 9), box.getMax());
        assertEquals(new WorldSize2d(10, 10), box.getSize());

        // Instantiating a 0 sized voxel box
        assertThrows(IllegalArgumentException.class, () -> new WorldBBox2d(0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new WorldBBox2d(0, 0, -1, -1));
    }

    @Test
    public void testContains() {
        WorldBBox2d box = new WorldBBox2d(-1, -2, 3, 4);

        //four points bounding the box
        assertTrue(box.contains(-1, -2));
        assertTrue(box.contains(1, -2));
        assertTrue(box.contains(1, 1));
        assertTrue(box.contains(-1, 1));

        //Point inside
        assertTrue(box.contains(0, -1));

        //Four points outside the box bounding it
        assertFalse(box.contains(-2, -2));
        assertFalse(box.contains(2, -2));
        assertFalse(box.contains(1, 2));
        assertFalse(box.contains(-1, 2));

        //Point outside
        assertFalse(box.contains(20, 10));

        //Same with other version

        assertTrue(box.contains(new WorldCoords2d(-1, -2)));
        assertTrue(box.contains(new WorldCoords2d(1, -2)));
        assertTrue(box.contains(new WorldCoords2d(1, 1)));
        assertTrue(box.contains(new WorldCoords2d(-1, 1)));

        //Point inside
        assertTrue(box.contains(new WorldCoords2d(0, -1)));

        //Four points outside the box bounding it
        assertFalse(box.contains(new WorldCoords2d(-2, -2)));
        assertFalse(box.contains(new WorldCoords2d(2, -2)));
        assertFalse(box.contains(new WorldCoords2d(1, 2)));
        assertFalse(box.contains(new WorldCoords2d(-1, 2)));

        //Point outside
        assertFalse(box.contains(new WorldCoords2d(20, 10)));
    }

    @Test
    public void testTo3d() {
        WorldBBox2d box = new WorldBBox2d(-1, -2, 4, 5);

        assertEquals(new WorldBBox3d(-1, -2, -3, 4, 5, 6), box.to3d(-3, 6), "BBOX to 3d");
    }
}
