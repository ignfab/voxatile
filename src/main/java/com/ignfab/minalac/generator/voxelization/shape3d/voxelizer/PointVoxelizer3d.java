package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

/**
 * A voxelizer that returns the points of a 3D shape.
 */
public class PointVoxelizer3d implements Shape3dVoxelizer {
    @Override
    public Iterable<? extends Positioned3d> voxelizeShape3d(Shape3d shape) {
        return shape.points();
    }
}
