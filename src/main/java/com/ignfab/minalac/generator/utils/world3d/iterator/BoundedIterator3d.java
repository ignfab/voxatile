package com.ignfab.minalac.generator.utils.world3d.iterator;

import com.ignfab.minalac.generator.utils.iterator.FilterIterator;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Voxel3d;

import java.util.Iterator;

/**
 * A composite iterator filtering the 3d voxels using a provided bounding box.
 *
 * @param <T> the type of voxels returned by resulting iterator
 * @param <U> the type of voxels returned by original iterator (may be more specific)
 */
public class BoundedIterator3d<T extends Voxel3d, U extends T> extends FilterIterator<T, U> {
    /**
     * Create a new iterator bounded by a box.
     *
     * @param iterator iterator to bound
     * @param bbox bounding box
     */
    public BoundedIterator3d(Iterator<U> iterator, WorldBBox3d bbox) {
        super(iterator, voxel -> bbox.contains(voxel.coords()));
    }
}
