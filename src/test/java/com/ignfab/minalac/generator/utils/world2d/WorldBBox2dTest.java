package com.ignfab.minalac.generator.utils.world2d;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldBBox2dTest {
    @Test
    @DisplayName("Test constructor taking origin and size as integers")
    public void testConstructorOriginSizeInt() {
        // Instantiating a new bounding box
        WorldBBox2d box = assertDoesNotThrow(() -> new WorldBBox2d(1, 2, 3, 4));
        assertEquals(new WorldCoords2d(1, 2), box.getMin());
        assertEquals(new WorldCoords2d(3, 5), box.getMax());
        assertEquals(new WorldSize2d(3, 4), box.getSize());

        // Instantiating a 0 sized bounding box
        assertDoesNotThrow(() -> new WorldBBox2d(0, 0, 0, 0));

        // Instantiating a negative sized bounding box
        assertThrows(IllegalArgumentException.class, () -> new WorldBBox2d(0, 0, -1, -1));
    }

    @Test
    @DisplayName("Test constructor taking a list of coordinates")
    public void testConstructorFirstOthers() {
        WorldBBox2d box;
        box = assertDoesNotThrow(() -> new WorldBBox2d(
            new WorldCoords2d(2, 3)
        ));
        assertEquals(new WorldCoords2d(2, 3), box.getMin());
        assertEquals(new WorldCoords2d(2, 3), box.getMax());
        assertEquals(new WorldSize2d(1, 1), box.getSize());

        box = assertDoesNotThrow(() -> new WorldBBox2d(
            new WorldCoords2d(1, -2),
            new WorldCoords2d(0, 3),
            new WorldCoords2d(-5, 0)
        ));

        assertEquals(new WorldCoords2d(-5, -2), box.getMin());
        assertEquals(new WorldCoords2d(1, 3), box.getMax());
        assertEquals(new WorldSize2d(7, 6), box.getSize());
    }

    @Test
    @DisplayName("Test contains() method")
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
    public void testContainsBBox() {
        WorldBBox2d bboxA = new WorldBBox2d(new WorldCoords2d(-2, -3), new WorldCoords2d(6, 7));
        WorldBBox2d bboxB = new WorldBBox2d(new WorldCoords2d(1, 0), new WorldCoords2d(3, 4));
        WorldBBox2d bboxC = new WorldBBox2d(new WorldCoords2d(4, 5), new WorldCoords2d(8, 9));
        WorldBBox2d bboxD = new WorldBBox2d(new WorldCoords2d(6, -3), new WorldCoords2d(14, 7));

        assertTrue(bboxA.contains(bboxA), "BBOX contains itself");

        assertTrue(bboxA.contains(bboxB), "BBOX A contains B: B is a strict subset of A");
        assertFalse(bboxB.contains(bboxA), "BBOX B does not contain A: B is a strict subset of A");

        assertFalse(bboxA.contains(bboxC), "BBOX A does not contains C: some elements of C, but not all, are in A");
        assertFalse(bboxC.contains(bboxA), "BBOX C does not contains A: some elements of A, but not all, are in C");

        assertFalse(bboxA.contains(bboxD), "BBOX A does not contains D: they share a line but are distinct");
        assertFalse(bboxB.contains(bboxD), "BBOX B does not contains D: they are distinct");

        // TODO: When EmptyBBOX is implemented, depending on the usage, decide whether or not it should be included in every BBOX
    }

    @Test
    @DisplayName("Test to3d() method")
    public void testTo3d() {
        WorldBBox2d box = new WorldBBox2d(-1, -2, 4, 5);

        assertEquals(new WorldBBox3d(-1, -2, -3, 4, 5, 6), box.to3d(-3, 6), "BBOX to 3d");
    }

    @Test
    @DisplayName("Test isEmpty() method")
    public void testIsEmpty() {
        assertFalse(new WorldBBox2d(-1, 2, 3, 2).isEmpty());
        assertTrue(new WorldBBox2d(-1, 2, 0, 2).isEmpty());
        assertTrue(new WorldBBox2d(-1, 2, 3, 0).isEmpty());
    }

    @Test
    @DisplayName("Test intersection() method")
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

        // Various intersecting boxes
        box = new WorldBBox2d(-2, -2, 5, 5);
        assertEquals(new WorldBBox2d(-2, -2, 3, 3), box.intersection(new WorldBBox2d(-3, -3, 4, 4)));
        assertEquals(new WorldBBox2d(0, -2, 3, 3), box.intersection(new WorldBBox2d(0, -3, 4, 4)));
        assertEquals(new WorldBBox2d(-2, 0, 3, 3), box.intersection(new WorldBBox2d(-3, 0, 4, 4)));
        assertEquals(new WorldBBox2d(0, 0, 3, 3), box.intersection(new WorldBBox2d(0, 0, 4, 4)));
    }
}
