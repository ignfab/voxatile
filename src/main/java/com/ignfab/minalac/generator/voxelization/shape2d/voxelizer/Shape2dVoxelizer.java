package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;

/**
 * Interface for voxelizers able to voxelize {@link Shape2dConvertible} into {@link Positioned2d}.
 */
public interface Shape2dVoxelizer {
    /**
     * Performs voxelization of a {@link Shape2dConvertible} into an iterable over 2d positions.
     *
     * @param convertible something convertible into {@code Shape2d}
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned2d> voxelize(Shape2dConvertible convertible);
}
