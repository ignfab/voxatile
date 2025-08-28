package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class ThinLine2dIteratorTest {
    @Test
    public void testIterator() {
        Line2d line = new Line2d(
            new WorldCoords2d(-1, -2),
            new WorldCoords2d(4, 3)
        );

        assertBrowsesAllOnce(
            Arrays.asList(
                new WorldCoords2d(-1, -2),
                new WorldCoords2d(0, -1),
                new WorldCoords2d(1, 0),
                new WorldCoords2d(2, 1),
                new WorldCoords2d(3, 2),
                new WorldCoords2d(4, 3)
            ),
            new ThinLine2dIterator(line)
        );
    }
}
