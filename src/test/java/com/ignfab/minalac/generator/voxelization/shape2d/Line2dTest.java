package com.ignfab.minalac.generator.voxelization.shape2d;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class Line2dTest {

    private void testLineConstruction(String name, WorldCoords2d start, WorldCoords2d end) {
        Line2d line = assertDoesNotThrow(() -> new Line2d(start, end), "No exception constructing " + name);
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
        assertEquals(new Vector2d(-1.0, 0.0), new Line2d(
            new WorldCoords2d(1, 2),
            new WorldCoords2d(-1, 2)
        ).direction());

        // Vertical line
        assertEquals(new Vector2d(0.0, 1.0), new Line2d(
            new WorldCoords2d(3, 4),
            new WorldCoords2d(3, 8)
        ).direction());

        // One voxel line (has no direction)
        assertEquals(Vector2d.ZERO, new Line2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).direction());
    }

    @Test
    public void testLenght() {
        // 3-4-5 rule
        assertEquals(5.0, new Line2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(13, 14)
        ).length(), 0.00001);

        // One voxel line
        assertEquals(0.0, new Line2d(
            new WorldCoords2d(10, 10),
            new WorldCoords2d(10, 10)
        ).length(), 0.00001);
    }

    @Test
    public void testAtIndex() {
        Line2d line;

        // We play with the 3,4,5 rule: 3²+4²=5² (a rectangle of 3x4 has a diagonal of 5).
        // Here we have a 9x12 line, which has a length of 15.
        // 9 size along X means there are 10 voxels (length is between voxels)

        line = new Line2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(line.start(), line.atIndex(0));
        assertEquals(line.end(), line.atIndex(15));

        assertEquals(new WorldCoords2d(0, 0), line.atIndex(5));
    }

    @Test
    public void testIndexAt() {
        Line2d line = new Line2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, 8)
        );

        assertEquals(0.0, line.indexAt(line.start().x(), line.start().y()), 0.00001);
        assertEquals(15.0, line.indexAt(line.end().x(), line.end().y()), 0.00001);
        assertEquals(5.0, line.indexAt(0, 0), 0.00001);
    }

    @Test
    public void testConvertLineRelative() {
        Line2d line;

        // Vertical line
        line = new Line2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(-3, -8)
        );

        assertEquals(new Vector2d(0.0, 3.0), line.convertLineRelative(-6, -4));
        assertEquals(new Vector2d(2.0, 0.0), line.convertLineRelative(-3, -6));
        assertEquals(new Vector2d(-1.0, 0.0), line.convertLineRelative(-3, -3));

        // Horizontal line
        line = new Line2d(
            new WorldCoords2d(-3, -4),
            new WorldCoords2d(6, -4)
        );

        assertEquals(new Vector2d(0.0, -2.0), line.convertLineRelative(-3, -2));
        assertEquals(new Vector2d(3.0, 0.0), line.convertLineRelative(0, -4));
        assertEquals(new Vector2d(-2.0, 0.0), line.convertLineRelative(-5, -4));
    }
}
