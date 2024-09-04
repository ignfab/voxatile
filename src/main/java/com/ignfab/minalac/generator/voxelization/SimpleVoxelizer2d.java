package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.BoundedIterator2d;

import java.util.Collections;
import java.util.Iterator;

/**
 * A simple voxelizer wrapping an iterable of 2d voxels.
 * It will use this iterable to return voxels in {@link #iterator()},
 * but no voxel will be returned in {@link #borders()}.
 * Additionally, it can take an optional bounding box to filter the returned voxels.
 *
 * @see BoundedIterator2d
 */
public class SimpleVoxelizer2d implements Voxelizer2d {
    private final Iterable<Voxel2d> iterable;
    private final WorldBBox2d bbox;

    /**
     * Creates a voxelizer wrapping the given iterable.
     * No filtering will be operated on the voxels.
     *
     * @param iterable the underlying iterable.
     */
    public SimpleVoxelizer2d(Iterable<Voxel2d> iterable) {
        this(iterable, null);
    }

    /**
     * Creates a voxelizer wrapping the given iterable and filtering on the given bounding box.
     * Any voxel returned by the underlying iterable outside the limits will be skipped.
     *
     * @param iterable the underlying iterable.
     * @param bbox the limits to filter voxels.
     * @see BoundedIterator2d
     */
    public SimpleVoxelizer2d(Iterable<Voxel2d> iterable, WorldBBox2d bbox) {
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
    public Iterator<Voxel2d> iterator() {
        Iterator<Voxel2d> iterator = iterable.iterator();
        return bbox == null ? iterator : new BoundedIterator2d<>(iterator, bbox);
    }

    /**
     * Returns an empty iterable.
     *
     * @return an empty iterable.
     */
    @Override
    public Iterable<LineVoxel2d> borders() {
        return Collections::emptyIterator;
    }

    /**
     * Returns main iterator.
     * No voxel is in border, so all voxels are inside.
     *
     * @return an iterator over all voxels
     */
    @Override
    public Iterable<Voxel2d> inside() {
        return this;
    }
}
