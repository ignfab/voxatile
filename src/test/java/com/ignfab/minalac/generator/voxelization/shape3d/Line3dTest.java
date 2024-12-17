package com.ignfab.minalac.generator.voxelization.shape3d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import static org.junit.jupiter.api.Assertions.*;

public class Line3dTest {

    private void testLineConstruction(String name, WorldCoords3d start, WorldCoords3d end) {
        Line3d line = assertDoesNotThrow(() -> new Line3d(start, end), "No exception constructing " + name);
        assertEquals(start, line.start(), "Correct start for " + name);
        assertEquals(end, line.end(), "Correct end for " + name);
        assertEquals(new WorldBBox3d(start, end), line.bbox(), "Correct bounding box for " + name);
    }

    @Test
    @DisplayName("Test constructor")
    public void testConstructor() {
        // One voxel line
        testLineConstruction("one voxel line", new WorldCoords3d(10, -20, 30), new WorldCoords3d(10, -20, 30));

        // X-axis line
        testLineConstruction("x-axis line", new WorldCoords3d(-10, 5, 15), new WorldCoords3d(10, 5, 15));

        // Y-axis line
        testLineConstruction("y-axis line", new WorldCoords3d(10, -5, 15), new WorldCoords3d(10, 15, 15));

        // Z-axis line
        testLineConstruction("z-axis line", new WorldCoords3d(10, -5, -15), new WorldCoords3d(10, -5, 5));

        // Random line
        testLineConstruction("random line", new WorldCoords3d(10, -20, 30), new WorldCoords3d(-30, 40, 60));
    }

    @Test
    @DisplayName("Test maxIndex")
    public void testMaxIndex() {
        Line3d line;

        // X normalized
        line = new Line3d(
            new WorldCoords3d(-5, 7, 4),
            new WorldCoords3d(8, -3, 6)
        );
        assertEquals(13, line.maxIndex()); // -5 to 8 -> index: 0 to 13

        // Y normalized
        line = new Line3d(
            new WorldCoords3d(-5, 7, 4),
            new WorldCoords3d(8, -10, 6)
        );
        assertEquals(17, line.maxIndex()); // 7 to -10 -> index: 0 to 17

        // Z normalized
        line = new Line3d(
            new WorldCoords3d(-5, 7, -9),
            new WorldCoords3d(8, -3, 6)
        );
        assertEquals(15, line.maxIndex()); // -9 to 6 -> index: 0 to 15
    }

    @Test
    @DisplayName("Test atIndex")
    public void testAtIndex() {
        Line3d line;

        line = new Line3d(
            new WorldCoords3d(3, 11, -5),
            new WorldCoords3d(-7, -4, 5)
        );

        assertEquals(line.start(), line.atIndex(0));
        assertEquals(line.end(), line.atIndex(line.maxIndex()));

        assertEquals(new WorldCoords3d(1, 8, -3), line.atIndex(3));
        assertEquals(-3, line.zAtIndex(3));
    }
}
