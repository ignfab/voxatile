package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unwraps an iterable/iterator of Iterable<T> into an Iterator<T>.
 *
 * @param <T> Type of iterable items
 */
public class UnwrapIterator<T> implements Iterator<T> {
    private final Iterator<? extends Iterable<? extends T>> iterables;
    private Iterator<? extends T> current;

    /**
     * Creates an UnwrapIterator from an iterable over iterables.
     *
     * @param iterables iterable to unwrap
     */
    public UnwrapIterator(Iterable<? extends Iterable<? extends T>> iterables) {
        this(iterables.iterator());
    }

    /**
     * Creates an UnwrapIterator from an iterator over iterables.
     *
     * @param iterables iterator to unwrap
     */
    public UnwrapIterator(Iterator<? extends Iterable<? extends T>> iterables) {
        this.iterables = iterables;
        moveOn();
    }

    private void moveOn() {
        while ((current == null || !current.hasNext()) && iterables.hasNext())
            current = iterables.next().iterator();
    }

    @Override
    public boolean hasNext() {
        return current != null && current.hasNext();
    }

    @Override
    public T next() {
        if (current == null || !current.hasNext())
            throw new NoSuchElementException();
        T element = current.next();
        moveOn();
        return element;
    }
}
