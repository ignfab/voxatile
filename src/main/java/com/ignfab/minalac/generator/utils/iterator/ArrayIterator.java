package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Simple iterator over an array.
 *
 * @param <T> the type of elements inside the array.
 */
public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int index;

    /**
     * Creates an iterator over the given array.
     *
     * @param array the array to iterate over.
     */
    public ArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T next() {
        if (index >= array.length)
            throw new NoSuchElementException();
        T element = array[index];
        index++;
        return element;
    }
}
