package com.ignfab.minalac.generator.utils.shape2d.iterator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.IteratorTester;
import com.ignfab.minalac.generator.utils.iterator.RemapIterator;
import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;

import java.util.Arrays;

public class Line2dIteratorTest {
    @Test
    @DisplayName("Test index")
    public void testIndex() {
        Line2d line = new Line2d(
            new WorldCoords2d(-1, -2),
            new WorldCoords2d(4, 3)
        );

        IteratorTester.assertBrowsesAllOnce(Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5 }),
            new RemapIterator<>(line.borderVoxels(), LineVoxel2d::index));
    }
}
