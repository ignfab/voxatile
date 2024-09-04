package com.ignfab.minalac.generator.utils.world3d;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

public class WorldBBox3dTest {
    @Test
    @DisplayName("Test constructor taking origin and size as integers")
    public void testConstructorOriginSizeInt() {
        // Instantiating a new bounding box
        WorldBBox3d box = assertDoesNotThrow(() -> new WorldBBox3d(1, 2, 3, 4, 5, 6));
        assertEquals(new WorldCoords3d(1, 2, 3), box.min());
        assertEquals(new WorldCoords3d(4, 6, 8), box.max());
        assertEquals(new WorldSize3d(4, 5, 6), box.size());

        // Instantiating a 0 sized bounding box
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
        assertEquals(new WorldCoords3d(1, 2, 3), box.min());
        assertEquals(new WorldCoords3d(1, 2, 3), box.max());
        assertEquals(new WorldSize3d(1, 1, 1), box.size());

        box = assertDoesNotThrow(() -> new WorldBBox3d(
            new WorldCoords3d(1, -2, 3),
            new WorldCoords3d(0, 4, -5),
            new WorldCoords3d(-6, 0, 0)
        ));

        assertEquals(new WorldCoords3d(-6, -2, -5), box.min());
        assertEquals(new WorldCoords3d(1, 4, 3), box.max());
        assertEquals(new WorldSize3d(8, 7, 9), box.size());
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
    public void testContainsBBox() {
        WorldBBox3d bboxA = new WorldBBox3d(new WorldCoords3d(-2, -3, -4), new WorldCoords3d(6, 7, 8));
        WorldBBox3d bboxB = new WorldBBox3d(new WorldCoords3d(1, 0, -1), new WorldCoords3d(3, 4, 5));
        WorldBBox3d bboxC = new WorldBBox3d(new WorldCoords3d(4, 5, 6), new WorldCoords3d(8, 9, 10));
        WorldBBox3d bboxD = new WorldBBox3d(new WorldCoords3d(6, -3, -4), new WorldCoords3d(14, 7, 8));

        assertTrue(bboxA.contains(bboxA), "BBOX contains itself");

        assertTrue(bboxA.contains(bboxB), "BBOX A contains B: B is a strict subset of A");
        assertFalse(bboxB.contains(bboxA), "BBOX B does not contain A: B is a strict subset of A");

        assertFalse(bboxA.contains(bboxC), "BBOX A does not contains C: some elements of C, but not all, are in A");
        assertFalse(bboxC.contains(bboxA), "BBOX C does not contains A: some elements of A, but not all, are in C");

        assertFalse(bboxA.contains(bboxD), "BBOX A does not contains D: they share a surface but are distinct");
        assertFalse(bboxB.contains(bboxD), "BBOX B does not contains D: they are distinct");

        // TODO: When EmptyBBOX is implemented, depending on the usage, decide whether or not it should be included in every BBOX
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
    @DisplayName("Test interects() method")
    public void testIntersects() {
        WorldBBox3d box;

        box = new WorldBBox3d(-2, 3, -1, 5, 7, 6);

        // Various non intersecting boxes
        assertFalse(box.intersects(new WorldBBox3d(3, -2, 5, 3, 3, 3)));
        assertFalse(box.intersects(new WorldBBox3d(-2, -2, -1, 3, 3, 3)));
        assertFalse(box.intersects(new WorldBBox3d(3, 3, -1, 3, 3, 3)));
        assertFalse(box.intersects(new WorldBBox3d(3, -2, 3, 3, 3, 3)));

        // Various intersecting boxes
        box = new WorldBBox3d(-2, -2, -2, 5, 5, 5);
        assertTrue(box.intersects(new WorldBBox3d(-3, -3, -3, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(0, -3, -3, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(-3, 0, -3, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(0, 0, -3, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(-3, -3, 0, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(0, -3, 0, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(-3, 0, 0, 4, 4, 4)));
        assertTrue(box.intersects(new WorldBBox3d(0, 0, 0, 4, 4, 4)));

        // One voxel intersections
        box = new WorldBBox3d(1, 2, 3, 2, 3, 4);
        assertTrue(box.intersects(new WorldBBox3d(0, 0, 0, 2, 3, 4)));
        assertTrue(box.intersects(new WorldBBox3d(2, 4, 6, 3, 2, 4)));
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

    @Test
    @DisplayName("Test surrounding() method")
    public void testSurrounding() {

        WorldBBox3d box;

        // Empty collection
        box = assertDoesNotThrow(() -> {
            return WorldBBox3d.surrounding(Collections.emptyList());
        });

        assertTrue(box.isEmpty());

        // Single Bounded collection
        box = assertDoesNotThrow(() -> {
            return WorldBBox3d.surrounding(Arrays.asList(new WorldBBox3d[] {
                new WorldBBox3d(1, 2, 3, 4, 5, 6)
            }));
        });

        assertEquals(new WorldBBox3d(1, 2, 3, 4, 5, 6), box);

        // Multiple Bounded collection
        box = assertDoesNotThrow(() -> {
            return WorldBBox3d.surrounding(Arrays.asList(new WorldBBox3d[] {
                new WorldBBox3d(1, 2, 3, 4, 5, 6),
                new WorldBBox3d(-1, -2, -3, 1, 2, 3)
            }));
        });

        assertEquals(new WorldBBox3d(-1, -2, -3, 6, 9, 12), box);

        // Empty boxes
        box = assertDoesNotThrow(() -> {
            return WorldBBox3d.surrounding(Arrays.asList(new WorldBBox3d[] {
                new WorldBBox3d(-10, -8, -6, 5, 4, 2),
                WorldBBox3d.EMPTY
            }));
        });

        assertEquals(new WorldBBox3d(-10, -8, -6, 5, 4, 2), box);

        // Empty boxes only
        box = assertDoesNotThrow(() -> {
            return WorldBBox3d.surrounding(Arrays.asList(new WorldBBox3d[] {
                WorldBBox3d.EMPTY,
                WorldBBox3d.EMPTY,
                WorldBBox3d.EMPTY
            }));
        });

        assertTrue(box.isEmpty());
    }
}

