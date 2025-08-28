package com.ignfab.minalac.generator.voxelization.shape2d.voxelizer;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Line2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2dConvertible;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThinLine2dIterator;

/**
 * A voxelizer for linear shapes, with no thickness.
 * Lines are drawn as thin as possible (one voxel, connecting by edges).
 */
public class ThinLinearVoxelizer2d {
    /**
     * Voxelizes a single line.
     *
     * @param line Line to voxelize
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Line2d line) {
        return () -> new ThinLine2dIterator(line);
    }

    /**
     * Voxelizes any other shapes, using line voxelization.
     *
     * @param convertible Something convertible to a {@code Shape2d}.
     * @return an iterable over voxelized positions.
     */
    public Iterable<Positioned2d> voxelize(Shape2dConvertible convertible) {
        return Iterables.unwrap(Iterables.remap(convertible.toShape2d().lines(), this::voxelize));
    }

}
