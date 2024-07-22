package com.ignfab.minalac.generator.utils.shape2d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class Line2dTest {

    private void testLineConstruction(String name, WorldCoords2d start, WorldCoords2d end) {
        Line2d line = assertDoesNotThrow(() -> {
                return new Line2d(start, end);
            }, "No exception contructing " + name);
        assertEquals(start, line.start(), "Correct start for " + name);
        assertEquals(end, line.end(), "Correct end for " + name);
        assertEquals(new WorldBBox2d(start, end), line.bbox(), "Correct bounding box for " + name);
    }

    @Test
    @DisplayName("Test constructor")
    public void testConstructor() {
        // One voxel line
        testLineConstruction("one voxel line", new WorldCoords2d(10, -20), new WorldCoords2d(10, -20));

        // Vertical line
        testLineConstruction("vertical line", new WorldCoords2d(10, -5), new WorldCoords2d(10, 15));

        // Horizontal line
        testLineConstruction("vertical line", new WorldCoords2d(-10, 5), new WorldCoords2d(10, 5));

        // Random line
        testLineConstruction("random line", new WorldCoords2d(10, -20), new WorldCoords2d(-30, 40));
    }

    @Test
    @DisplayName("Test maxIndex")
    public void testMaxIndex() {
        Line2d line;

        line = new Line2d(
            new WorldCoords2d(-5, 7),
            new WorldCoords2d(8, -10)
        );
        assertEquals(17, line.maxIndex()); // 7 + 10

        line = new Line2d(
            new WorldCoords2d(-5, 7),
            new WorldCoords2d(8, -3)
        );
        assertEquals(13, line.maxIndex()); // 8 + 5
    }

    @Test
    @DisplayName("Test atIndex")
    public void testAtIndex() {
        Line2d line;

        line = new Line2d(
            new WorldCoords2d(3, 11),
            new WorldCoords2d(-7, -4)
        );

        assertEquals(line.start(), line.atIndex(0));
        assertEquals(line.end(), line.atIndex(line.maxIndex()));
    }

    @Test
    @DisplayName("Test intersection")
    public void testIntersection() {
        Line2d line;
        Line2d.Intersection intersection;

        // Horizontal line
        line = new Line2d(
            new WorldCoords2d(30, 20),
            new WorldCoords2d(-10, 20)
        );

        assertNull(line.intersection(19));
        assertNull(line.intersection(21));
        intersection = line.intersection(20);

        assertEquals(-10, intersection.start());
        assertEquals(30, intersection.end());
        assertFalse(intersection.bottom());
        assertFalse(intersection.top());

        // Vertical line
        line = new Line2d(
            new WorldCoords2d(15, -5),
            new WorldCoords2d(15, 10)
        );

        assertNull(line.intersection(-6));
        assertNull(line.intersection(11));

        intersection = line.intersection(0);
        assertNotNull(intersection);
        assertEquals(15, intersection.start());
        assertEquals(15, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());

        intersection = line.intersection(-5);
        assertNotNull(intersection);
        assertTrue(intersection.bottom());
        assertFalse(intersection.top());

        intersection = line.intersection(10);
        assertNotNull(intersection);
        assertFalse(intersection.bottom());
        assertTrue(intersection.top());

        // One voxel line
        line = new Line2d(
            new WorldCoords2d(10, -20),
            new WorldCoords2d(10, -20)
        );
        assertNull(line.intersection(-21));
        assertNull(line.intersection(-19));
        assertNotNull(line.intersection(-20));

        // Several voxel intersections
        line = new Line2d(
            new WorldCoords2d(-5, -2),
            new WorldCoords2d(5, 2)
        );
        intersection = line.intersection(0);
        assertEquals(-1, intersection.start());
        assertEquals(1, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());

        // Same test, the other way
        line = new Line2d(
            new WorldCoords2d(-5, 2),
            new WorldCoords2d(5, -2)
        );
        intersection = line.intersection(0);
        assertEquals(-1, intersection.start());
        assertEquals(1, intersection.end());
        assertTrue(intersection.bottom());
        assertTrue(intersection.top());
    }
}
