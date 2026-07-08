package com.ignfab.minalac.generator.voxelization.shape3d.voxelizer;

import java.util.Collections;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3dConvertible;

/**
 * Interface for voxelizers able to voxelize {@link Shape3dConvertible} models into {@link Positioned3d}.
 */
public interface Shape3dVoxelizer extends Voxelizer3d {
    /**
     * Perform voxelization of a {@link Shape3d} into an iterable over 3d positions.
     *
     * @param shape shape to voxelize
     * @return iterable over corresponding voxel positions
     */
    Iterable<? extends Positioned3d> voxelizeShape3d(Shape3d shape);

    @Override
    default Iterable<? extends Positioned3d> voxelize(Model model) {
        if (model instanceof Shape3dConvertible convertible)
            return voxelizeShape3d(convertible.toShape3d());
        else
            return Collections.emptyList();
    }
}
