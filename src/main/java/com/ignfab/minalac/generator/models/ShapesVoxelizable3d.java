package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;

/**
 * An object (likely a model) voxelizable by {@code ShapesVoxelizer3d}.
 */
public interface ShapesVoxelizable3d extends Voxelizable3d {
    @Override
    ShapesVoxelizer3d voxelize3d(WorldBBox3d bbox);
}
