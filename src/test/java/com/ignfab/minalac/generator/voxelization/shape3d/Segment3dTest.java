package com.ignfab.minalac.generator.voxelization.shape3d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

import static org.junit.jupiter.api.Assertions.*;

public class Segment3dTest {

    private void testSegmentConstruction(String name, WorldCoords3d start, WorldCoords3d end) {
        Segment3d segment = assertDoesNotThrow(() -> new Segment3d(start, end), "No exception constructing " + name);
        assertEquals(start, segment.start(), "Correct start for " + name);
        assertEquals(end, segment.end(), "Correct end for " + name);
        assertEquals(new WorldBBox3d(start, end), segment.bbox(), "Correct bounding box for " + name);
    }

    @Test
    public void testConstructor() {
        // One voxel segment
        testSegmentConstruction("one voxel segment", new WorldCoords3d(10, -20, 5), new WorldCoords3d(10, -20, 5));

        // Random segment
        testSegmentConstruction("random segment", new WorldCoords3d(10, -20, 5), new WorldCoords3d(-30, 40, -12));
    }

    @Test
    public void testDirection() {
        assertEquals(new Vector3d(-1.0, 0.0, 0.0), new Segment3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(-5, 2, 3)
        ).direction());

        assertEquals(new Vector3d(0.0, 1.0, 0.0), new Segment3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 4, 3)
        ).direction());

        assertEquals(new Vector3d(0.0, 0.0, -1.0), new Segment3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, -3)
        ).direction());

        // One voxel segment (has no direction)
        assertEquals(new Vector3d(0.0, 0.0, 0.0), new Segment3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(10, 10, 10)
        ).direction());

    }

    @Test
    public void testLength() {
        assertEquals(7.0, new Segment3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(12, 13, 16)
        ).length(), 0.00001);

        // One voxel segment
        assertEquals(0.0, new Segment3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(10, 10, 10)
        ).length(), 0.00001);
    }

    @Test
    public void testAtIndex() {

        // We play with fact that: 2²+3²+6²=7²
        // Here we have a 6x9x18 segment, which has a length of 21.
        // (6 wide means 7 voxels from center to center)

        Segment3d segment = new Segment3d(
            new WorldCoords3d(-2, -3, -6),
            new WorldCoords3d(4, 6, 12)
        );

        assertEquals(segment.start(), segment.atIndex(0));
        assertEquals(segment.end(), segment.atIndex(21));

        assertEquals(new WorldCoords3d(0, 0, 0), segment.atIndex(7));
    }

    @Test
    public void testTo2d() {
        Segment2d segment2d = new Segment3d(new WorldCoords3d(1, 2, 3), new WorldCoords3d(4, 5, 6)).to2d();

        assertEquals(new WorldCoords2d(1, 2), segment2d.start());
        assertEquals(new WorldCoords2d(4, 5), segment2d.end());
    }
}
