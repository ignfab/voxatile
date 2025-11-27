package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThinSegment2dIterator;

/**
 * A voxelizer for linear shapes, with no thickness.
 * Lines are drawn as thin as possible (one voxel, connecting by edges).
 */
public class ThinLinearVoxelizer2d implements Shape2dVoxelizer {

    /**
     * Voxelizes a line string (or linear ring).
     *
     * @param lineString Line string to voxelize
     * @return an iterable over voxelized positions
     */
    public Iterable<Positioned2d> voxelize(LineString2d lineString) {
        return Iterables.flatMap(lineString.segments(), (segment) -> () -> new ThinSegment2dIterator(segment));
    }

    @Override
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.flatMap(convertible.toShape2d().lineStrings(), this::voxelize);
    }

}
