package com.ignfab.minalac.generator.utils.world2d.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * An iterator clipping another 2d iterator to a given bounding box.
 *
 * @param <T> Type of returned values
 */
public class WorldBBox2dClipIterator<T extends Positioned2d> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final WorldBBox2d limits;
    private T next;

    /**
     * Creates a new {@code WorldBBox2dClipIterator}.
     *
     * @param iterator Iterator to clip
     * @param limits Limits to clip to
     */
    public WorldBBox2dClipIterator(Iterator<T> iterator, WorldBBox2d limits) {
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
