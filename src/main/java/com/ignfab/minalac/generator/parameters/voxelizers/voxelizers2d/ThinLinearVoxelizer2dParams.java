package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThinLinearVoxelizer2d;

/**
 * Parameters for a {@link ThinLinearVoxelizer2dParams}.
 */
public class ThinLinearVoxelizer2dParams extends Voxelizer2dParams {

    @Override
    public Voxelizer2d create() {
        return new ThinLinearVoxelizer2d();
    }
}
