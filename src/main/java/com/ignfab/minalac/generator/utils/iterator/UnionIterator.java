package com.ignfab.minalac.generator.utils.iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator concatenating different iterators.
 *
 * @param <T> Type of iterable items
 */
public class UnionIterator<T> implements Iterator<T> {
    private final Iterator<? extends Iterator<? extends T>> iterators;
    private Iterator<? extends T> current;

    /**
     * Creates an UnionIterator from varable number of iterators.
     *
     * @param iterators Iterators to be concatenated
     */
    @SafeVarargs
    public UnionIterator(Iterator<? extends T>... iterators) {
        this.iterators = Arrays.asList(iterators).iterator();
        moveOn();
    }

    private void moveOn() {
        while ((current == null || !current.hasNext()) && iterators.hasNext())
            current = iterators.next();
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
