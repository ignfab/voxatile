package com.ignfab.minalac.generator.voxelization;

import java.util.Iterator;

/**
 * A 3d voxelizer provides a way to iterate over 3d voxels.
 * The default {@link #iterator()} returns all voxels while
 * {@link #borders()} iterates only on voxels on the edges.
 * <p>
 * The voxels returned by iterators are not guaranteed to be unique:
 * It may contain duplicate coordinate.
 */
public interface Voxelizer3d extends Iterable<Voxel3d> {
    /**
     * Returns an iterator over all the voxels in this object.
     *
     * @return the global iterator of this object.
     */
    @Override
    Iterator<Voxel3d> iterator();

    /**
     * Returns an iterable over voxels on the edges of this object.
     * Example usage:
     * <pre>{@code
     *  Voxelizer3d voxelizer = ...;
     *  for (IndexedVoxel3d edgeVoxel : voxelizer.borders()) {
     *      // Code using edgeVoxel here...
     *  }
     * }</pre>
     *
     * @return the border iterable of this object.
     */
    Iterable<LineVoxel3d> borders();
}
