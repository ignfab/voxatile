package com.ignfab.minalac.generator.utils.world2d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestWorldBBox2d {
    @Test
    public void testConstructor() {
        // Instanciating a new voxel box
        new WorldBBox2d(0, 0, 10, 10);

        // Instanciating a 0 sized voxel box
        try {
            new WorldBBox2d(0, 0, 0, 0);
            fail("IllegalArgumentException expected!");
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }

        try {
            new WorldBBox2d(0, 0, -1, -1);
            fail("IllegalArgumentException expected!");
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }
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
}