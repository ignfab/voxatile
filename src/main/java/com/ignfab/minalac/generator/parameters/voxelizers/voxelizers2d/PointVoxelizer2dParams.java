package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.PointVoxelizer2d;

/**
 * Parameters for a {@link PointVoxelizer2d}.
 */
public class PointVoxelizer2dParams extends Voxelizer2dParams {

    @Override
    public Voxelizer2d create() {
        return new PointVoxelizer2d();
    }
}
