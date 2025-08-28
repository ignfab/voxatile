package com.ignfab.minalac.generator.utils.world3d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * An iterator clipping another 3d iterator to a given bounding box.
 *
 * @param <T> Type of returned values
 */
public class WorldBBox3dClipIterator<T extends Positioned3d> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final WorldBBox3d limits;
    private T next;

    /**
     * Creates a new {@code WorldBBox3dClipIterator}.
     *
     * @param iterator Iterator to clip
     * @param limits Limits to clip to
     */
    public WorldBBox3dClipIterator(Iterator<T> iterator, WorldBBox3d limits) {
        this.iterator = iterator;
        this.limits = limits;
        moveOn();
    }

    private void moveOn() {
        while (iterator.hasNext()) {
            next = iterator.next();
            if (limits.contains(next))
                return;
        }
        next = null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        if (next == null)
            throw new NoSuchElementException();
        T result = next;
        moveOn();
        return result;
    }
}
