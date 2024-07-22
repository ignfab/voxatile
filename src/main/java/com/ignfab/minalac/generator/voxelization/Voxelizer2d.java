package com.ignfab.minalac.generator.voxelization;

import java.util.Iterator;

/**
 * A 2d voxelizer provides a way to iterate over 2d voxels.
 * The default {@link #iterator()} returns all voxels while
 * {@link #borders()} iterates only on voxels on the edges.
 * <p>
 * The voxels returned by iterators are not guaranteed to be unique:
 * It may contain duplicate coordinate.
 */
public interface Voxelizer2d extends Iterable<Voxel2d> {
    /**
     * Returns an iterator over all the voxels in this object.
     *
     * @return the global iterator of this object.
     */
    @Override
    Iterator<Voxel2d> iterator();

    /**
     * Returns an iterable over voxels on the edges of this object.
     * Example usage:
     * <pre>{@code
     *  Voxelizer2d voxelizer = ...;
     *  for (IndexedVoxel2d edgeVoxel : voxelizer.borders()) {
     *      // Code using edgeVoxel here...
     *  }
     * }</pre>
     *
     * @return the border iterable of this object.
     */
    Iterable<IndexedVoxel2d> borders();

    /**
     * Returns an iterable over voxels strictly inside this object
     * (only polygons have inside voxels).
     * Example usage:
     * <pre>{@code
     *  Voxelizer2d voxelizer = ...;
     *  for (Voxel2d voxel : voxelizer.inside()) {
     *      // Code using voxel here...
     *  }
     * }</pre>
     *
     * @return the inside iterable of this object.
     */
    Iterable<Voxel2d> inside();
}
