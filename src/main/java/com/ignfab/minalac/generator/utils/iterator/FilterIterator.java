package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A composite iterator filtering items from another iterator.
 *
 * @param <T> the type of items returned by resulting iterator
 * @param <U> the type of items returned by original iterator (may be more specific)
 */
public class FilterIterator<T, U extends T> implements Iterator<T> {
    private final Iterator<U> iterator;
    private final Predicate<U> filter;

    private U current;

    /**
     * Creates a new iterator skipping items according to a condition function.
     *
     * @param iterable the iterable to get items from
     * @param filter the condition predicate returning true when item should be kept
     */
    public FilterIterator(Iterable<U> iterable, Predicate<U> filter) {
        this(iterable.iterator(), filter);
    }

    /**
     * Creates a new iterator skipping items according to a condition function.
     *
     * @param iterator the iterator to get elements from
     * @param filter the condition predicate returning true when item should be kept
     */
    public FilterIterator(Iterator<U> iterator, Predicate<U> filter) {
        this.iterator = iterator;
        this.filter = filter;
        moveOn();
    }

    private void moveOn() {
        while (iterator.hasNext()) {
            current = iterator.next();
            if (filter.test(current))
                return;
        }
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
        T next = current;
        moveOn();
        return next;
    }
}
