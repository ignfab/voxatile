package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * Parameters for a {@link SurfaceVoxelizer2d}.
 */
public class SurfaceVoxelizer2dParams extends Voxelizer2dParams {

    @Override
    public Voxelizer2d create() {
        return new SurfaceVoxelizer2d();
    }
}
