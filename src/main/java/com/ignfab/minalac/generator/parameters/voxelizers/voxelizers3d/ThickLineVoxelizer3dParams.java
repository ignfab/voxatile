package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.voxelization.Voxelizer3d;
import com.ignfab.minalac.generator.voxelization.shape3d.voxelizer.ThickLinearIndexedVoxelizer2d5;

/**
 * Parameters for a {@link ThickLinearIndexedVoxelizer2d5}.
 */
public class ThickLineVoxelizer3dParams extends Voxelizer3dParams {

    /**
     * Thickness of the line, in voxels.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double thickness = 1.0;

    @Override
    public Voxelizer3d create() {
        return new ThickLinearIndexedVoxelizer2d5(thickness);
    }
}
