package com.ignfab.minalac.generator.voxelization.shape2d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class Segment2dTest {

    private void testSegmentConstruction(String name, WorldCoords2d start, WorldCoords2d end) {
        Segment2d segment = assertDoesNotThrow(() -> new Segment2d(start, end), "No exception constructing " + name);
        assertEquals(start, segment.start(), "Correct start for " + name);
        assertEquals(end, segment.end(), "Correct end for " + name);
        assertEquals(new WorldBBox2d(start, end), segment.bbox(), "Correct bounding box for " + name);
    }

    @Test
    public void testConstructor() {
        // One voxel segment
        testSegmentConstruction("one voxel segment", new WorldCoords2d(10, -20), new WorldCoords2d(10, -20));

        // Random segment
        testSegmentConstruction("random segment", new WorldCoords2d(10, -20), new WorldCoords2d(-30, 40));
    }

    @Test
    public void testDirection() {
        // Horizontal segment
        assertEquals(new Vector2d(-1.0, 0.0), new Segment2d(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(-1, 2)
        ).direction());

        // Vertical segment
        assertEquals(new Vector2d(0.0, 1.0), new Segment2d(
            new WorldCoords2d(3, 4),
            new WorldCoords2d(3, 8)
        ).direction());

        // One voxel segment (has no direction)
        assertEquals(Vector2d.ZERO, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).direction());
    }

    @Test
    public void testLength() {
        // 3-4-5 rule
        assertEquals(5.0, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(13, 14)
        ).length(), 0.00001);

        // One voxel segment
        assertEquals(0.0, new Segment2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).length(), 0.00001);
    }

    @Test
    public void testAtIndex() {
        Segment2d segment;

        // We play with the 3,4,5 rule: 3²+4²=5² (a rectangle of 3x4 has a diagonal of 5).
        // Here we have a 9x12 segment, which has a length of 15.
        // 9 size along X means there are 10 voxels (length is between voxels)

        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(segment.start(), segment.atIndex(0));
        assertEquals(segment.end(), segment.atIndex(15));

        assertEquals(new WorldCoords2d(0, 0), segment.atIndex(5));
    }

    @Test
    public void testNearestPointIndex() {
        Segment2d segment;

        // Vertical segment
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(-3, -8)
        );

        assertEquals(0.0, segment.nearestPointIndex(-6, -4), 0.00001);
        assertEquals(2.0, segment.nearestPointIndex(-3, -6), 0.00001);
        assertEquals(-1.0, segment.nearestPointIndex(-3, -3), 0.00001);

        // Horizontal segment
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -4)
        );

        assertEquals(0.0, segment.nearestPointIndex(-3, -2), 0.00001);
        assertEquals(3.0, segment.nearestPointIndex(0, -4), 0.00001);
        assertEquals(-2.0, segment.nearestPointIndex(-5, -4), 0.00001);

        // Oblique segment
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(0.0, segment.nearestPointIndex(segment.start().x(), segment.start().y()), 0.00001);
        assertEquals(15.0, segment.nearestPointIndex(segment.end().x(), segment.end().y()), 0.00001);
        assertEquals(5.0, segment.nearestPointIndex(0, 0), 0.00001);
    }

    @Test
    public void testSignedDistanceTo() {
        Segment2d segment;

        // Vertical segment
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(-3, -8)
        );

        assertEquals(3.0, segment.signedDistanceTo(-6, -4), 0.00001);
        assertEquals(0.0, segment.signedDistanceTo(-3, -6), 0.00001);
        assertEquals(0.0, segment.signedDistanceTo(-3, -3), 0.00001);

        // Horizontal segment
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -4)
        );

        assertEquals(-2.0, segment.signedDistanceTo(-3, -2), 0.00001);
        assertEquals(0.0, segment.signedDistanceTo(0, -4), 0.00001);
        assertEquals(0.0, segment.signedDistanceTo(-5, -4), 0.00001);

        // Check distance is positive along normal and negative in the oposite direction
        segment = new Segment2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -8)
        );

        assertEquals(1.0, segment.signedDistanceTo(segment.normal().add(segment.start().toVector())), 0.00001);
        assertEquals(-1.0, segment.signedDistanceTo(segment.normal().opposite().add(segment.start().toVector())), 0.00001);
    }
}
