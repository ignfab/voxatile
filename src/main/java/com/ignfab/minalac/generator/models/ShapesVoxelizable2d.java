package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * An object (likely a model) voxelizable by {@code ShapesVoxelizer2d}.
 */
public interface ShapesVoxelizable2d extends Voxelizable2d {
    @Override
    ShapesVoxelizer2d voxelize2d(WorldBBox2d bbox);
}
