package com.ignfab.minalac.generator.voxelization.shape2d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;

/**
 * Interface for voxel shapes in 2 dimensions.
 */
public interface Shape2d {
    /**
     * Returns an iterator over all voxels in this shape.
     *
     * @return the global iterator of this shape.
     */
    default Iterable<Positioned2d> allVoxels() {
        return Iterables.union(borderVoxels(), insideVoxels());
    }

    /**
     * Returns an iterable over border voxels in this shape.
     *
     * @return the border iterable of this shape.
     */
    default Iterable<LineVoxel2d> borderVoxels() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an iterable over inside voxels in this shape.
     *
     * @return the inside iterable of this shape.
     */
    default Iterable<Positioned2d> insideVoxels() {
        return Collections::emptyIterator;
    }
}
