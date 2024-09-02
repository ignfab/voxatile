package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearVoxel2d;

import java.util.Arrays;

public class Line2dIteratorTest {
    @Test
    @DisplayName("Test index")
    public void testIndex() {
        Line2d line = new Line2d(
            new WorldCoords2d(-1, -2),
            new WorldCoords2d(4, 3)
        );

        assertBrowsesAllOnce(
            Arrays.asList(0, 1, 2, 3, 4, 5),
            Iterators.remap(line.borderVoxels().iterator(), LinearVoxel2d::index)
        );
    }
}
