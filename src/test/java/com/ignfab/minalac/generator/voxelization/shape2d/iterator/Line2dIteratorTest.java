package com.ignfab.minalac.generator.voxelization.shape2d.iterator;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class Line2dIteratorTest {
    @Test
    @DisplayName("Test index")
    public void testIndex() {
        Line2d line = new Line2d(
            new WorldCoords2d(-1, -2),
            new WorldCoords2d(4, 3)
        );

        assertBrowsesAllOnce(
            List.of(0, 1, 2, 3, 4, 5),
            Iterators.remap(line.borderVoxels().iterator(), LineVoxel2d::index)
        );
    }
}
