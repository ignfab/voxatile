package com.ignfab.minalac.generator.utils.shape2d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.voxelization.IndexedVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Iterator;

public interface Shape2d extends Iterable<Voxel2d> {
    /**
     * Returns an iterator over all voxels in this shape.
     *
     * @return the global iterator of this shape.
     */
    @Override
    default Iterator<Voxel2d> iterator() {
        return MultiIterator.concat(borderVoxels(), insideVoxels());
    }

    /**
     * Returns an iterable over border voxels in this shape.
     *
     * @return the border iterable of this shape.
     */
    Iterable<IndexedVoxel2d> borderVoxels();

    /**
     * Returns an iterable over inside voxels in this shape.
     *
     * @return the inside iterable of this shape.
     */
    Iterable<Voxel2d> insideVoxels();
}
