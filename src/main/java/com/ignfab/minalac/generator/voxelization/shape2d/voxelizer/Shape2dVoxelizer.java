package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import java.util.Collections;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;

/**
 * Interface for voxelizers able to voxelize {@link Shape2dConvertible} models into {@link Positioned2d}.
 */
public interface Shape2dVoxelizer extends Voxelizer2d {
    /**
     * Performs voxelization of a {@link Shape2d} into an iterable over 2d positions.
     *
     * @param shape shape to voxelize
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned2d> voxelizeShape2d(Shape2d shape);

    @Override
    default Iterable<? extends Positioned2d> voxelize(Model model) {
        if (model instanceof Shape2dConvertible convertible)
            return voxelizeShape2d(convertible.toShape2d());
        else
            return Collections.emptyList();
    }
}
