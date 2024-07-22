package com.ignfab.minalac.generator.utils.shape2d.iterator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;

import static org.junit.jupiter.api.Assertions.*;

public class Line2dIteratorTest {
    @Test
    @DisplayName("Test index")
    public void testIndex() {
        Line2d line = new Line2d(
            new WorldCoords2d(-1, -2),
            new WorldCoords2d(4, 3)
        );

        int indexCount = 6;
        boolean[] used = new boolean[indexCount];

        for (IndexedVoxel2d voxel : line) {
            assertFalse(used[voxel.index()], "Same index twice: " + voxel.index());
            used[voxel.index()] = true;
            indexCount--;
        }

        assertEquals(0, indexCount, "Not all indexes have been browsed by iterator");
    }

}
