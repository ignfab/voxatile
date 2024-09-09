package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple iterator giving a single element.
 *
 * @param <T> the type of the element return by this iterator.
 */
public class SingletonIterator<T> implements Iterator<T> {
    private final T element;
    private boolean hasNext = true;

    /**
     * Creates a new iterator returning only the given element, once.
     *
     * @param element the only element return by this iterator.
     */
    public SingletonIterator(T element) {
        this.element = element;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNext)
            throw new NoSuchElementException();
        hasNext = false;
        return element;
    }
}
