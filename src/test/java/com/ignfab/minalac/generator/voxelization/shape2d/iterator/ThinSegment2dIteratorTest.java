package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class ThinSegment2dIteratorTest {
    @Test
    public void testIterator() {
        Segment2d segment = new Segment2d(
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
            new ThinSegment2dIterator(segment)
        );
    }
}
