package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.BuildingVoxelizer;

/**
 * An object (likely a model) voxelizable by {@link BuildingVoxelizer}.
 */
public interface BuildingVoxelizable extends Voxelizable3d {
    @Override
    BuildingVoxelizer voxelize3d(WorldBBox3d bbox);
}
