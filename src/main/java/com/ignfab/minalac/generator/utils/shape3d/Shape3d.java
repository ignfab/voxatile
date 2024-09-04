package com.ignfab.minalac.generator.utils.shape3d;

import com.ignfab.minalac.generator.utils.iterator.MultiIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

/**
 * Interface for voxel shapes in 3 dimensions.
 */
public interface Shape3d {

    /**
     * Returns shape bounding box.
     *
     * @return bounding box.
     */
    WorldBBox3d bbox();

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
    Iterable<LineVoxel3d> borderVoxels();

    /**
     * Returns an iterable over inside voxels in this shape.
     *
     * @return the inside iterable of this shape.
     */
    Iterable<Voxel3d> insideVoxels();
}
