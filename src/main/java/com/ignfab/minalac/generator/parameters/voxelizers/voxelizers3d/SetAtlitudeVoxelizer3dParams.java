package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.heightmaps.ReadableHeightmapParams;
import com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d.Voxelizer2dParams;
import com.ignfab.minalac.generator.voxelization.SetAltitudeVoxelizer3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * Parameters for a {@link SetAltitudeVoxelizer3d}.
 */
public class SetAtlitudeVoxelizer3dParams extends Voxelizer3dParams {
    /**
     * Voxels (from voxelizer) to turn to 3d.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public Voxelizer2dParams voxels;

    /**
     * Altitude (from heightmap) to set as z-coordinate.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams altitude;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param voxels voxelizer to transform
     * @param altitude heightmap to fetch altitudes from
     */
    @ConstructorProperties({ "voxels", "altitude" })
    public SetAtlitudeVoxelizer3dParams(Voxelizer2dParams voxels, ReadableHeightmapParams altitude) {
        this.voxels = voxels;
        this.altitude = altitude;
    }

    @Override
    public void validate() {
        altitude.validate();
        voxels.validate();
    }

    @Override
    public Voxelizer3d create() {
        return new SetAltitudeVoxelizer3d(voxels.create(), altitude.create(Generation.current().heightmaps()));
    }
}
