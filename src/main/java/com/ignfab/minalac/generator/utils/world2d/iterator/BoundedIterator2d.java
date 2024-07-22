package com.ignfab.minalac.generator.utils.world2d.iterator;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A composite iterator filtering the 2d voxels using a provided bounding box.
 *
 * @param <T> the type of voxels returned by the iterator.
 */
public class BoundedIterator2d<T extends Voxel2d> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final WorldBBox2d bbox;
    private T current;

    /**
     * Creates a new iterator filtering the result of the given one using the given bounding box.
     *
     * @param iterator the iterator to get voxels.
     * @param bbox the bounding box to filter voxels.
     */
    public BoundedIterator2d(Iterator<T> iterator, WorldBBox2d bbox) {
        this.iterator = iterator;
        this.bbox = bbox;
        moveOn();
    }

    private void moveOn() {
        if (iterator.hasNext()) {
            current = iterator.next();
            if (!bbox.contains(current.coords()))
                moveOn();
        } else
            current = null;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        if (current == null)
            throw new NoSuchElementException();
        T element = current;
        moveOn();
        return element;
    }
}
