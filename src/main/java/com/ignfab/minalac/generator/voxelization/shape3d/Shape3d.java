package com.ignfab.minalac.generator.voxelization.shape3d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;

/**
 * Interface for voxel shapes in 3 dimensions.
 */
public interface Shape3d {
    /**
     * Returns an iterator over all voxels in this shape.
     *
     * @return the global iterator of this shape.
     */
    default Iterable<Positioned3d> allVoxels() {
        return Iterables.union(borderVoxels(), insideVoxels());
    }

    /**
     * Returns an iterable over border voxels in this shape.
     *
     * @return the border iterable of this shape.
     */
    default Iterable<LineVoxel3d> borderVoxels() {
        return Collections::emptyIterator;
    };

    /**
     * Returns an iterable over inside voxels in this shape.
     *
     * @return the inside iterable of this shape.
     */
    default Iterable<Positioned3d> insideVoxels() {
        return Collections::emptyIterator;
    }
}
