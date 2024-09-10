package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.iterator.FilterIterator;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Iterator;

/**
 * A composite iterator filtering the 2d voxels using a provided bounding box.
 *
 * @param <T> the type of voxels returned by resulting iterator
 * @param <U> the type of voxels returned by original iterator (may be more specific)
 */
public class BoundedIterator2d<T extends Voxel2d, U extends T> extends FilterIterator<T, U> {
    /**
     * Create a new iterator bounded by a box.
     *
     * @param iterator iterator to bound
     * @param bbox bounding box
     */
    public BoundedIterator2d(Iterator<U> iterator, WorldBBox2d bbox) {
        super(iterator, voxel -> bbox.contains(voxel.coords()));
    }
}
