package com.ignfab.minalac.generator.utils.world3d.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.IteratorTester;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

public class BoundedIterator2dTest {
    @Test
    void test() {
        List<Voxel3d> list = Arrays.asList(
            new Voxel3d.Impl(new WorldCoords3d(2, 3, 4)),
            new Voxel3d.Impl(new WorldCoords3d(0, 1, 2)),
            new Voxel3d.Impl(new WorldCoords3d(6, 7, 8)),
            new Voxel3d.Impl(new WorldCoords3d(4, 5, 6)),
            new Voxel3d.Impl(new WorldCoords3d(8, 9, 10))
        );
        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(
                new Voxel3d.Impl(new WorldCoords3d(4, 5, 6)),
                new Voxel3d.Impl(new WorldCoords3d(6, 7, 8))
            ),
            new BoundedIterator3d<Voxel3d>(list.iterator(), new WorldBBox3d(2, 4, 6, 5, 4, 3))
        );

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new BoundedIterator3d<Voxel3d>(list.iterator(), new WorldBBox3d(-2, -4, -5, 1, 3, 5))
        );

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new BoundedIterator3d<Voxel3d>(list.iterator(), WorldBBox3d.EMPTY)
        );
    }
}
