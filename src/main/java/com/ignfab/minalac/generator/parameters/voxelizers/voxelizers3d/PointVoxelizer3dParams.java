package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d;

import com.ignfab.minalac.generator.voxelization.Voxelizer3d;
import com.ignfab.minalac.generator.voxelization.shape3d.voxelizer.PointVoxelizer3d;

/**
 * Parameters for a {@link PointVoxelizer3d}.
 */
public class PointVoxelizer3dParams extends Voxelizer3dParams {

    @Override
    public Voxelizer3d create() {
        return new PointVoxelizer3d();
    }
}
