package com.ignfab.minalac.generator.voxelization;

import java.util.Collections;
import java.util.Iterator;

/**
 * A basic voxelizer that does not return any voxel.
 *
 * @see #INSTANCE
 */
public class EmptyVoxelizer2d implements Voxelizer2d {
    /**
     * The default instance that can be reused,
     * because this object does not hold any data.
     */
    public static final EmptyVoxelizer2d INSTANCE = new EmptyVoxelizer2d();

    /**
     * Returns an empty iterator.
     *
     * @return an empty iterator.
     */
    @Override
    public Iterator<Voxel2d> iterator() {
        return Collections.emptyIterator();
    }

    /**
     * Returns an empty iterable.
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<IndexedVoxel2d> borders() {
        return Collections::emptyIterator;
    }

    /**
     * Returns an empty iterable.
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<Voxel2d> inside() {
        return Collections::emptyIterator;
    }
}
