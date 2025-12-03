package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

import static org.junit.jupiter.api.Assertions.*;

public class ThickSegment2dIteratorTest {
    private static int limitX = 30;
    private static int limitY = 30;
    private static List<Segment2d> lines = List.of(
        new Segment2d(new WorldCoords2d(5, 6), new WorldCoords2d(21, 23)),
        new Segment2d(new WorldCoords2d(25, 6), new WorldCoords2d(8, 23)),
        new Segment2d(new WorldCoords2d(11, 23), new WorldCoords2d(19, 23)),
        new Segment2d(new WorldCoords2d(21, 5), new WorldCoords2d(21, 18))
    );

    private boolean[][] renderIterator(Iterator<Positioned2d> iterator) {
        boolean[][] result = new boolean[limitX][limitY];

        while (iterator.hasNext()) {
            WorldCoords2d pos = iterator.next().coords();
            if (pos.x() >= 0 && pos.x() < limitX && pos.y() >= 0 && pos.y() < limitY)
                result[pos.x()][pos.y()] = true;
        }
        return result;
    }

    // We won't tests exacly what this iterator does because it could vary without seriously affecting visual effect.
    // Instead we will test some of its characteristics.

    @Test
    @DisplayName("Test all resulting voxels are connected")
    public void testConnected() {

        for (Segment2d line : lines) {
            boolean[][] result = renderIterator(new ThickSegment2dIterator(line, 5, line.normal(), line.normal().opposite()));

            for (int x = 1; x < limitX - 1; x++)
                for (int y = 1; y < limitY - 1; y++)
                    if (result[x][y] && !(result[x + 1][y] || result[x][y + 1] || result[x - 1][y] || result[x][y - 1]))
                        fail("Line %s: Expected a connected voxel at (%d, %d).".formatted(line, x, y));
        }
    }

    // Test we can escape to the border from a given positon (x, y) in a given direction (dx, dy)
    private boolean testEscape(boolean[][] data, int x, int y, int dx, int dy) {
        do {
            x = x + dx;
            y = y + dy;
            if (x < 0 || y < 0 || x >= limitX || y >= limitY)
                return true;
        } while (!data[x][y]);
        return false;
    }

    @Test
    @DisplayName("Test there is no hole in resulting voxels")
    public void testNoHoles() {

        // As we are on a line, each non set voxel should be on top/left/bottom/right most position or it's a hole.
        // This wouldn't work with more complex shapes.

        for (Segment2d line : lines) {
            boolean[][] result = renderIterator(new ThickSegment2dIterator(line, 5, line.normal(), line.normal().opposite()));

            for (int x = 1; x < limitX - 1; x++)
                for (int y = 1; y < limitY - 1; y++)
                    if (!result[x][y])
                        assertTrue(
                            testEscape(result, x, y, 1, 0)
                            || testEscape(result, x, y, -1, 0)
                            || testEscape(result, x, y, 0, 1)
                            || testEscape(result, x, y, 0, -1),
                            "Line %s: Unexpeted hole at (%d, %d).".formatted(line, x, y));
        }
    }
}
