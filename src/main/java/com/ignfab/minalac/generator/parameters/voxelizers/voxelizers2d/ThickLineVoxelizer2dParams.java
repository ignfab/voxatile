package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThickLinearIndexedVoxelizer2d;

/**
 * Parameters for a {@link ThickLinearIndexedVoxelizer2d}.
 */
public class ThickLineVoxelizer2dParams extends Voxelizer2dParams {

    /**
     * Thickness of the line, in voxels.
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public double thickness = 1.0;

    @Override
    public Voxelizer2d create() {
        return new ThickLinearIndexedVoxelizer2d(thickness);
    }
}
