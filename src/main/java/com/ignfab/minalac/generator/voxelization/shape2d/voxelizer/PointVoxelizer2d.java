package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;

/**
 * A voxelizer that returns the points of a 2D shape.
 */
public class PointVoxelizer2d implements Shape2dVoxelizer {
    @Override
    public Iterable<? extends Positioned2d> voxelizeShape2d(Shape2d shape) {
        return shape.points();
    }
}
