package com.ignfab.minalac.generator.utils.shape3d;

import java.util.Collections;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

/**
 * Interface for voxel shapes in 3 dimensions.
 */
public interface Shape3d {
    /**
     * Returns an iterator over all voxels in this shape.
     *
     * @return the global iterator of this shape.
     */
    default Iterable<Voxel3d> allVoxels() {
        return () -> MultiIterator.concat(borderVoxels(), insideVoxels());
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
    default Iterable<Voxel3d> insideVoxels() {
        return Collections::emptyIterator;
    }
}
