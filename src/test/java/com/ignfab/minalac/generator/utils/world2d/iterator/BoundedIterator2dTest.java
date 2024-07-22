package com.ignfab.minalac.generator.utils.world2d.iterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.iterator.IteratorTester;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

public class BoundedIterator2dTest {
    @Test
    void test() {
        List<Voxel2d> list = Arrays.asList(
            new Voxel2d.Impl(new WorldCoords2d(2, 3)),
            new Voxel2d.Impl(new WorldCoords2d(0, 1)),
            new Voxel2d.Impl(new WorldCoords2d(6, 7)),
            new Voxel2d.Impl(new WorldCoords2d(4, 5)),
            new Voxel2d.Impl(new WorldCoords2d(8, 9))
        );
        IteratorTester.assertBrowsesAllOnce(
            Arrays.asList(
                new Voxel2d.Impl(new WorldCoords2d(4, 5)),
                new Voxel2d.Impl(new WorldCoords2d(6, 7))
            ),
            new BoundedIterator2d<Voxel2d>(list.iterator(), new WorldBBox2d(2, 4, 5, 4))
        );

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new BoundedIterator2d<Voxel2d>(list.iterator(), new WorldBBox2d(-2, -4, 1, 3))
        );

        IteratorTester.assertBrowsesAllOnce(
            Collections.emptyList(),
            new BoundedIterator2d<Voxel2d>(list.iterator(), WorldBBox2d.EMPTY)
        );
    }
}
