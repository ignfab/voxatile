package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;

/**
 * A voxelizer converting a model into 2d voxels.
 */
public interface Voxelizer2d {
    /**
     * Performs voxelization of a {@link Model} into an iterable over 2d positions.
     * <p>
     * If unable to voxelize model, returns an empty iterator.
     *
     * @param model model to voxelize
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned2d> voxelize(Model model);
}
