package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;

/**
 * A voxelizer converting a model into  3d voxels.
 */
public interface Voxelizer3d {
    /**
     * Performs voxelization of a {@link Model} into an iterable over 3d positions.
     * <p>
     * If unable to voxelize model, returns an empty iterator.
     *
     * @param model model to voxelize
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned3d> voxelize(Model model);
}
