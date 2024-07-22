package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.function.Function;

/**
 * A composite iterator transforming the result into another value using a provided function.
 *
 * @param <T> the type of elements returned by the underlying iterator.
 * @param <U> the transformed type of elements this iterator will return.
 */
public class RemapIterator<T, U> implements Iterator<U> {
    private final Iterator<T> iterator;
    private final Function<T, U> mapper;

    /**
     * Creates a new iterator transforming the result of the given iterable using the given function.
     *
     * @param iterable the iterable to get elements.
     * @param mapper the function to transform elements.
     */
    public RemapIterator(Iterable<T> iterable, Function<T, U> mapper) {
        this(iterable.iterator(), mapper);
    }

    /**
     * Creates a new iterator transforming the result of the given one using the given function.
     *
     * @param iterator the iterator to get elements.
     * @param mapper the function to transform elements.
     */
    public RemapIterator(Iterator<T> iterator, Function<T, U> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public U next() {
        return mapper.apply(iterator.next());
    }
}
