package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox2dTest {
    @Test
    public void testConstructor() {
        // Instantiating a new bounding box
        WorldBBox2d box = assertDoesNotThrow(() -> new WorldBBox2d(0, 0, 10, 10));
        assertEquals(new WorldCoords2d(0, 0), box.getMin());
        assertEquals(new WorldCoords2d(9, 9), box.getMax());
        assertEquals(new WorldSize2d(10, 10), box.getSize());

        // Instanciating a 0 sized bounding box
        assertDoesNotThrow(() -> new WorldBBox2d(0, 0, 0, 0));

        // Instantiating a negative sized bounding box
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

    @Test
    public void testIsEmpty() {
        assertFalse(new WorldBBox2d(-1, 2, 3, 2).isEmpty());
        assertTrue(new WorldBBox2d(-1, 2, 0, 2).isEmpty());
        assertTrue(new WorldBBox2d(-1, 2, 3, 0).isEmpty());
    }

    @Test
    public void testIntersection() {
        WorldBBox2d box;
        WorldBBox2d box2;

        box = new WorldBBox2d(-2, 3, 5, 7);
        box2 = new WorldBBox2d(-3, 2, 7, 8);

        // Contained intersections and commutativity
        assertEquals(box, box.intersection(box2));
        assertEquals(box, box2.intersection(box));

        // Various non intersecting boxes
        assertTrue(box.intersection(new WorldBBox2d(3, -2, 3, 3)).isEmpty());
        assertTrue(box.intersection(new WorldBBox2d(-2, -2, 3, 3)).isEmpty());
        assertTrue(box.intersection(new WorldBBox2d(3, 3, 3, 3)).isEmpty());

        // Various intersectig boxes
        box = new WorldBBox2d(-2, -2, 5, 5);
        assertEquals(new WorldBBox2d(-2, -2, 3, 3), box.intersection(new WorldBBox2d(-3, -3, 4, 4)));
        assertEquals(new WorldBBox2d(0, -2, 3, 3), box.intersection(new WorldBBox2d(0, -3, 4, 4)));
        assertEquals(new WorldBBox2d(-2, 0, 3, 3), box.intersection(new WorldBBox2d(-3, 0, 4, 4)));
        assertEquals(new WorldBBox2d(0, 0, 3, 3), box.intersection(new WorldBBox2d(0, 0, 4, 4)));
    }
}
