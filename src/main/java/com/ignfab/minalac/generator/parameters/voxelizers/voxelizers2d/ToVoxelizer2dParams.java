package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d.Voxelizer3dParams;
import com.ignfab.minalac.generator.voxelization.ToVoxelizer2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * Parameters for a {@link ToVoxelizer2d}.
 */
public class ToVoxelizer2dParams extends Voxelizer2dParams {
    /**
     * Voxels (from voxelizer) to turn to 2d.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public Voxelizer3dParams voxels;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param voxels voxelizer to transform
     */
    @ConstructorProperties({ "voxels" })
    public ToVoxelizer2dParams(Voxelizer3dParams voxels) {
        this.voxels = voxels;
    }

    @Override
    public void validate() {
        voxels.validate();
    }

    @Override
    public Voxelizer2d create() {
        return new ToVoxelizer2d(voxels.create());
    }
}
