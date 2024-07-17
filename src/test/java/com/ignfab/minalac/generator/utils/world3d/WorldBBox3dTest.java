package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox3dTest {
    @Test
    @DisplayName("Test constructor taking origin and size as integers")
    public void testConstructorOriginSizeInt() {
        // Instantiating a new bounding box
        WorldBBox3d box = assertDoesNotThrow(() -> new WorldBBox3d(1, 2, 3, 4, 5, 6));
        assertEquals(new WorldCoords3d(1, 2, 3), box.getMin());
        assertEquals(new WorldCoords3d(4, 6, 8), box.getMax());
        assertEquals(new WorldSize3d(4, 5, 6), box.getSize());

        // Instanciating a 0 sized bounding box
        assertDoesNotThrow(() -> new WorldBBox3d(0, 0, 0, 0, 0, 0));

        // Instantiating a negative sized bounding box
        assertThrows(IllegalArgumentException.class, () -> new WorldBBox3d(0, 0, 0, -1, -1, -1));
    }

    @Test
    @DisplayName("Test constructor taking a list of coordinates")
    public void testConstructorFirstOthers() {
        WorldBBox3d box;
        box = assertDoesNotThrow(() -> new WorldBBox3d(
            new WorldCoords3d(1, 2, 3)
        ));
        assertEquals(new WorldCoords3d(1, 2, 3), box.getMin());
        assertEquals(new WorldCoords3d(1, 2, 3), box.getMax());
        assertEquals(new WorldSize3d(1, 1, 1), box.getSize());

        box = assertDoesNotThrow(() -> new WorldBBox3d(
            new WorldCoords3d(1, -2, 3),
            new WorldCoords3d(0, 4, -5),
            new WorldCoords3d(-6, 0, 0)
        ));

        assertEquals(new WorldCoords3d(-6, -2, -5), box.getMin());
        assertEquals(new WorldCoords3d(1, 4, 3), box.getMax());
        assertEquals(new WorldSize3d(8, 7, 9), box.getSize());
    }

    @Test
    @DisplayName("Test contains() method")
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
    @DisplayName("Test to2d() method")
    public void testTo2d() {
        WorldBBox3d box = new WorldBBox3d(-1, -2, -3, 4, 5, 6);

        assertEquals(new WorldBBox2d(-1, -2, 4, 5), box.to2d(), "BBOX to 2d");
    }

    @Test
    @DisplayName("Test isEmpty() method")
    public void testIsEmpty() {
        assertFalse(new WorldBBox3d(-1, 2, 0, 3, 2, 4).isEmpty());
        assertTrue(new WorldBBox3d(-1, 2, 0, 0, 2, 4).isEmpty());
        assertTrue(new WorldBBox3d(-1, 2, 0, 3, 0, 4).isEmpty());
        assertTrue(new WorldBBox3d(-1, 2, 0, 3, 2, 0).isEmpty());
    }

    @Test
    @DisplayName("Test intersection() method")
    public void testIntersection() {
        WorldBBox3d box;
        WorldBBox3d box2;

        box = new WorldBBox3d(-2, 3, -1, 5, 7, 6);
        box2 = new WorldBBox3d(-3, 2, -2, 7, 9, 8);

        // Contained intersections and commutativity
        assertEquals(box, box.intersection(box2));
        assertEquals(box, box2.intersection(box));

        // Various non intersecting boxes
        assertTrue(box.intersection(new WorldBBox3d(3, -2, 5, 3, 3, 3)).isEmpty());
        assertTrue(box.intersection(new WorldBBox3d(-2, -2, -1, 3, 3, 3)).isEmpty());
        assertTrue(box.intersection(new WorldBBox3d(3, 3, -1, 3, 3, 3)).isEmpty());
        assertTrue(box.intersection(new WorldBBox3d(3, -2, 3, 3, 3, 3)).isEmpty());

        // Various intersectig boxes
        box = new WorldBBox3d(-2, -2, -2, 5, 5, 5);
        assertEquals(new WorldBBox3d(-2, -2, -2, 3, 3, 3), box.intersection(new WorldBBox3d(-3, -3, -3, 4, 4, 4)));
        assertEquals(new WorldBBox3d(0, -2, -2, 3, 3, 3), box.intersection(new WorldBBox3d(0, -3, -3, 4, 4, 4)));
        assertEquals(new WorldBBox3d(-2, 0, -2, 3, 3, 3), box.intersection(new WorldBBox3d(-3, 0, -3, 4, 4, 4)));
        assertEquals(new WorldBBox3d(0, 0, -2, 3, 3, 3), box.intersection(new WorldBBox3d(0, 0, -3, 4, 4, 4)));
        assertEquals(new WorldBBox3d(-2, -2, 0, 3, 3, 3), box.intersection(new WorldBBox3d(-3, -3, 0, 4, 4, 4)));
        assertEquals(new WorldBBox3d(0, -2, 0, 3, 3, 3), box.intersection(new WorldBBox3d(0, -3, 0, 4, 4, 4)));
        assertEquals(new WorldBBox3d(-2, 0, 0, 3, 3, 3), box.intersection(new WorldBBox3d(-3, 0, 0, 4, 4, 4)));
        assertEquals(new WorldBBox3d(0, 0, 0, 3, 3, 3), box.intersection(new WorldBBox3d(0, 0, 0, 4, 4, 4)));
    }
}

