package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;

/**
 * A 2d voxelizer flattening voxels from a 3d voxelizer.
 */
public class ToVoxelizer2d implements Voxelizer2d {

    private final Voxelizer3d voxelizer;

    /**
     * Creates a new {@code ToVoxelizer2d}.
     *
     * @param voxelizer voxelizer to flatted to 2d
     */
    public ToVoxelizer2d(Voxelizer3d voxelizer) {
        this.voxelizer = voxelizer;
    }

    @Override
    public Iterable<Positioned2d> voxelize(Model model) {
        return Iterables.remap(
            voxelizer.voxelize(model),
            (pos) -> pos.coords().to2d()
        );
    }
}
