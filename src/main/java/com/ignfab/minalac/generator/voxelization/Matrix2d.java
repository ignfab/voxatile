package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * An iterable 2d voxel matrix that eventually holds generic typed values.
 * <p>
 * A voxel with no value is usually skipped (iterator takes that in charge).
 *
 * @param <T> type of value associated to voxels
 */
public interface Matrix2d<T> extends Iterable<Matrix2d.Value<T>>, Bounded2d {

    /**
     * Gets hold value at given position.
     *
     * @param coords position to get value from
     *
     * @return value or null if none
     */
    T get(WorldCoords2d coords);

    /**
     * Iterator on existing values.
     *
     * @return an iterator over every voxel of the matrix skipping those with null value
     */
    @Override
    default Matrix2dIterator<T> iterator() {
        return new Matrix2dIterator<>(this);
    }

    /**
     * A 2d voxel with an associated value.
     *
     * @param coords position of the voxel
     * @param value value associated to position
     *
     * @param <T> type of value associated to voxels
     */
    record Value<T>(WorldCoords2d coords, T value) implements Positioned2d {}
}
