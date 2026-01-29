package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.IndexedPosition2d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.IndexedPosition3d;

public interface Voxelizer {
    default Iterable<Positioned2d> voxelize2d(Model model) {
        return Iterators.remap(voxelize3d(model), (Positioned3d p) -> p.to2d());
    };
    Iterable<Positioned3d> voxelize3d(Model model);
    Iterable<IndexedPosition2d> voxelizeIndexed2d(Model model);
    Iterable<IndexedPosition3d> voxelizeIndexed3d(Model model);
}
