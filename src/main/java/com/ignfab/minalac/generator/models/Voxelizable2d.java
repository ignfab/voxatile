package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * Represents an object that can be transformed into 2d voxels using a voxelizer.
 * This interface usually applies to models.
 */
public interface Voxelizable2d {
    /**
     * Provides a voxelizer that returns voxels representing this object in 2d.
     * The voxelizer must only return voxels inside the given bounding box.
     * <p>
     * Note that the voxelizer is not required to:
     * <ul>
     * <li>return a voxel for every position in the given bounding box
     * <li>return a single voxel per position (it may return duplicates)
     * </ul>
     *
     * @param bbox the limits for this voxelization.
     * @return a {@link Voxelizer2d} to represent this object in 2d voxels.
     */
    Voxelizer2d voxelize2d(WorldBBox2d bbox);
}
