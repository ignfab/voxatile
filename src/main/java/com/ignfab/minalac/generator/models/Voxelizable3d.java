package com.ignfab.minalac.generator.models;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * Represents an object that can be transformed into 3d voxels using a voxelizer.
 * This interface usually applies to models.
 * Most objects representable in 3d can also be represented in 2d, but this is not always the case!
 */
public interface Voxelizable3d {
    /**
     * Provides a voxelizer that returns voxels representing this object in 3d.
     * The voxelizer must only return voxels inside the given bounding box.
     * <p>
     * Note that the voxelizer is not required to:
     * <ul>
     * <li>return a voxel for every position in the given bounding box
     * <li>return a single voxel per position (it may return duplicates)
     * </ul>
     *
     * @param bbox the limits for this voxelization.
     * @return a {@link Voxelizer3d} to represent this object in 3d voxels.
     */
    Voxelizer3d voxelize3d(WorldBBox3d bbox);
}
