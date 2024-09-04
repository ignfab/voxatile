package com.ignfab.minalac.generator.voxelization;

import java.util.Collections;
import java.util.Iterator;

/**
 * A basic voxelizer that does not return any voxel.
 *
 * @see #INSTANCE
 */
public class EmptyVoxelizer3d implements Voxelizer3d {
    /**
     * The default instance that can be reused,
     * because this object does not hold any data.
     */
    public static final EmptyVoxelizer3d INSTANCE = new EmptyVoxelizer3d();

    /**
     * Returns an empty iterator.
     *
     * @return an empty iterator.
     */
    @Override
    public Iterator<Voxel3d> iterator() {
        return Collections.emptyIterator();
    }

    /**
     * Returns an empty iterable.
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<LineVoxel3d> borders() {
        return Collections::emptyIterator;
    }
}
