package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3dConvertible;

/**
 * Interface for voxelizers able to voxelize {@link Shape3dConvertible} into {@link Positioned3d}.
 */
public interface Shape3dVoxelizer {
    /**
     * Perform voxelization of a {@link Shape3dConvertible} into an iterable over 3d positions.
     *
     * @param convertible something convertible into {@code Shape3d}
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned3d> voxelize(Shape3dConvertible convertible);
}
