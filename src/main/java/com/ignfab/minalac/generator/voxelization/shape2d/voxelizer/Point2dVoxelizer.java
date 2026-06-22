package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;

/**
 * A voxelizer that returns the points of a 2D shape.
 */
public class Point2dVoxelizer implements Shape2dVoxelizer {
    @Override
    public Iterable<? extends Positioned2d> voxelize(Shape2dConvertible convertible) {
        return convertible.toShape2d().points();
    }
}
