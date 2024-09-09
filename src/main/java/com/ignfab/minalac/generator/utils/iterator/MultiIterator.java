package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Composite iterator allowing to merge multiple iterators of compatible type.
 * Iterator-ception!
 *
 * @param <T> the type of elements this iterator will return.
 */
public class MultiIterator<T> implements Iterator<T> {
    private final Iterator<? extends Iterable<? extends T>> iterables;
    private Iterator<? extends T> current;

    /**
     * Creates a new iterator merging the results of the given iterators.
     *
     * @param iterables the iterators to merge.
     */
    public MultiIterator(Iterable<? extends Iterable<? extends T>> iterables) {
        this(iterables.iterator());
    }

    /**
     * Creates a new iterator merging the results of the given iterators.
     *
     * @param iterables the iterators to merge.
     */
    public MultiIterator(Iterator<? extends Iterable<? extends T>> iterables) {
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

    /**
     * Creates a new iterator merging the results of the given iterators.
     *
     * @param iterables the iterators to merge.
     * @return a new {@link MultiIterator} over all given iterators.
     * @param <T> the type of elements the returned iterator will return.
     */
    @SafeVarargs
    public static <T> MultiIterator<T> concat(Iterable<? extends T>... iterables) {
        return new MultiIterator<>(new ArrayIterator<>(iterables));
    }
}
