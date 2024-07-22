package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.iterator.BoundedIterator3d;

import java.util.Collections;
import java.util.Iterator;

/**
 * A simple voxelizer wrapping an iterable of 3d voxels.
 * It will use this iterable to return voxels in {@link #iterator()},
 * but no voxel will be returned in {@link #borders()}.
 * Additionally, it can take an optional bounding box to filter the returned voxels.
 *
 * @see BoundedIterator3d
 */
public class SimpleVoxelizer3d implements Voxelizer3d {
    private final Iterable<Voxel3d> iterable;
    private final WorldBBox3d bbox;

    /**
     * Creates a voxelizer wrapping the given iterable.
     * No filtering will be operated on the voxels.
     *
     * @param iterable the underlying iterable.
     */
    public SimpleVoxelizer3d(Iterable<Voxel3d> iterable) {
        this(iterable, null);
    }

    /**
     * Creates a voxelizer wrapping the given iterable and filtering on the given bounding box.
     * Any voxel returned by the underlying iterable outside the limits will be skipped.
     *
     * @param iterable the underlying iterable.
     * @param bbox the limits to filter voxels.
     * @see BoundedIterator3d
     */
    public SimpleVoxelizer3d(Iterable<Voxel3d> iterable, WorldBBox3d bbox) {
        this.iterable = iterable;
        this.bbox = bbox;
    }

    /**
     * Returns an iterator based upon the underlying iterable.
     * If a bounding box is set, the returned iterator will filter out any outside voxel.
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<Voxel3d> iterator() {
        Iterator<Voxel3d> iterator = iterable.iterator();
        return bbox == null ? iterator : new BoundedIterator3d<>(iterator, bbox);
    }

    /**
     * Returns an empty iterable.
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<IndexedVoxel3d> borders() {
        return Collections::emptyIterator;
    }
}
