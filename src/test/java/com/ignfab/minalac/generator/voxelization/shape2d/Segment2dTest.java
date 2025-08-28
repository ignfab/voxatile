package com.ignfab.minalac.generator.voxelization.shape2d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class Segment2dTest {

    private void testLineConstruction(String name, WorldCoords2d start, WorldCoords2d end) {
        Segment2d line = assertDoesNotThrow(() -> new Segment2d(start, end), "No exception constructing " + name);
        assertEquals(start, line.start(), "Correct start for " + name);
        assertEquals(end, line.end(), "Correct end for " + name);
        assertEquals(new WorldBBox2d(start, end), line.bbox(), "Correct bounding box for " + name);
    }

    @Test
    public void testConstructor() {
        // One voxel line
        testLineConstruction("one voxel line", new WorldCoords2d(10, -20), new WorldCoords2d(10, -20));

        // Random line
        testLineConstruction("random line", new WorldCoords2d(10, -20), new WorldCoords2d(-30, 40));
    }

    @Test
    public void testDirection() {
        // Horizontal line
        assertEquals(new Vector2d(-1.0, 0.0), new Segment2d(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(-1, 2)
        ).direction());

        // Vertical line
        assertEquals(new Vector2d(0.0, 1.0), new Segment2d(
            new WorldCoords2d(3, 4),
            new WorldCoords2d(3, 8)
        ).direction());

        // One voxel line (has no direction)
        assertEquals(Vector2d.ZERO, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).direction());
    }

    @Test
    public void testLenght() {
        // 3-4-5 rule
        assertEquals(5.0, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(13, 14)
        ).length(), 0.00001);

        // One voxel line
        assertEquals(0.0, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).length(), 0.00001);
    }

    @Test
    public void testAtIndex() {
        Segment2d line;

        // We play with the 3,4,5 rule: 3²+4²=5² (a rectangle of 3x4 has a diagonal of 5).
        // Here we have a 9x12 line, which has a length of 15.
        // 9 size along X means there are 10 voxels (length is between voxels)

        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(line.start(), line.atIndex(0));
        assertEquals(line.end(), line.atIndex(15));

        assertEquals(new WorldCoords2d(0, 0), line.atIndex(5));
    }

    @Test
    public void testNearestPointIndex() {
        Segment2d line;

        // Vertical line
        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(-3, -8)
        );

        assertEquals(0.0, line.nearestPointIndex(-6, -4), 0.00001);
        assertEquals(2.0, line.nearestPointIndex(-3, -6), 0.00001);
        assertEquals(-1.0, line.nearestPointIndex(-3, -3), 0.00001);

        // Horizontal line
        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -4)
        );

        assertEquals(0.0, line.nearestPointIndex(-3, -2), 0.00001);
        assertEquals(3.0, line.nearestPointIndex(0, -4), 0.00001);
        assertEquals(-2.0, line.nearestPointIndex(-5, -4), 0.00001);

        // Oblique line
        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(0.0, line.nearestPointIndex(line.start().x(), line.start().y()), 0.00001);
        assertEquals(15.0, line.nearestPointIndex(line.end().x(), line.end().y()), 0.00001);
        assertEquals(5.0, line.nearestPointIndex(0, 0), 0.00001);
    }

    @Test
    public void testSignedDistanceTo() {
        Segment2d line;

        // Vertical line
        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(-3, -8)
        );

        assertEquals(-3.0, line.signedDistanceTo(-6, -4), 0.00001);
        assertEquals(0.0, line.signedDistanceTo(-3, -6), 0.00001);
        assertEquals(0.0, line.signedDistanceTo(-3, -3), 0.00001);

        // Horizontal line
        line = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -4)
        );

        assertEquals(2.0, line.signedDistanceTo(-3, -2), 0.00001);
        assertEquals(0.0, line.signedDistanceTo(0, -4), 0.00001);
        assertEquals(0.0, line.signedDistanceTo(-5, -4), 0.00001);
    }
}
