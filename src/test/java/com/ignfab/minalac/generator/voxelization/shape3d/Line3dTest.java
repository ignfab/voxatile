package com.ignfab.minalac.generator.voxelization.shape3d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

import static org.junit.jupiter.api.Assertions.*;

public class Line3dTest {

    private void testLineConstruction(String name, WorldCoords3d start, WorldCoords3d end) {
        Line3d line = assertDoesNotThrow(() -> new Line3d(start, end), "No exception constructing " + name);
        assertEquals(start, line.start(), "Correct start for " + name);
        assertEquals(end, line.end(), "Correct end for " + name);
        assertEquals(new WorldBBox3d(start, end), line.bbox(), "Correct bounding box for " + name);
    }

    @Test
    public void testConstructor() {

        // One voxel line
        testLineConstruction("one voxel line", new WorldCoords3d(10, -20, 5), new WorldCoords3d(10, -20, 5));

        // Random line
        testLineConstruction("random line", new WorldCoords3d(10, -20, 5), new WorldCoords3d(-30, 40, -12));
    }

    @Test
    public void testDirection() {
        assertEquals(new Vector3d(-1.0, 0.0, 0.0), new Line3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(-5, 2, 3)
        ).direction());

        assertEquals(new Vector3d(0.0, 1.0, 0.0), new Line3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 4, 3)
        ).direction());

        assertEquals(new Vector3d(0.0, 0.0, -1.0), new Line3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(1, 2, -3)
        ).direction());

        // One voxel line (has no direction)
        assertEquals(new Vector3d(0.0, 0.0, 0.0), new Line3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(10, 10, 10)
        ).direction());

    }

    @Test
    public void testLenght() {
        assertEquals(7.0, new Line3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(12, 13, 16)
        ).length(), 0.00001);

        // One voxel line
        assertEquals(0.0, new Line3d(
            new WorldCoords3d(10, 10, 10),
            new WorldCoords3d(10, 10, 10)
        ).length(), 0.00001);
    }

    @Test
    public void testAtIndex() {

        // We play with fact that: 2²+3²+6²=7²
        // Here we have a 6x9x18 line, which has a length of 21.
        // (6 wide means 7 voxels from center to center)

        Line3d line = new Line3d(
            new WorldCoords3d(-2, -3, -6),
            new WorldCoords3d(4, 6, 12)
        );

        assertEquals(line.start(), line.atIndex(0));
        assertEquals(line.end(), line.atIndex(21));

        assertEquals(new WorldCoords3d(0, 0, 0), line.atIndex(7));
    }

    @Test
    public void testTo2d() {
        Line2d line = new Line3d(
            new WorldCoords3d(1, 2, 3),
            new WorldCoords3d(4, 5, 6)
        ).to2d();

        assertEquals(new WorldCoords2d(1, 2), line.start());
        assertEquals(new WorldCoords2d(4, 5), line.end());
    }
}
