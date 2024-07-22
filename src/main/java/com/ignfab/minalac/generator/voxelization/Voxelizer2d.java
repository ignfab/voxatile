package com.ignfab.minalac.generator.voxelization;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;

/**
 * A 2d voxelizer provides a way to iterate over 2d voxels.
 * This is the minimal voxelizer, which only tells which voxels are in the model.
 * <p>
 * The voxels returned by iterators are not guaranteed to be unique:
 * It may contain duplicate coordinate.
 */
public interface Voxelizer2d extends Iterable<Positioned2d> {
    /**
     * Returns an iterator over all the voxels in this object.
     *
     * @return the global iterator of this object.
     */
    @Override
    Iterator<Positioned2d> iterator();
}
