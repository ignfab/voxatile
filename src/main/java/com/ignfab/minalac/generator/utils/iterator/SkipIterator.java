package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * A composite iterator skiping some items of another iterator.
 *
 * @param <T> the type of items to iterate on
 */
public class SkipIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;
    private final Function<T, Boolean> skip;

    private T current;

    /**
     * Creates a new iterator skipping items according to a condition function.
     *
     * @param iterable the iterable to get items from
     * @param skip the condition function returning true when item should be skipped
     */
    public SkipIterator(Iterable<T> iterable, Function<T, Boolean> skip) {
        this(iterable.iterator(), skip);
    }

    /**
     * Creates a new iterator skipping items according to a condition function.
     *
     * @param iterator the iterator to get elements from
     * @param skip the condition function, element is skipped when it returns true
     */
    public SkipIterator(Iterator<T> iterator, Function<T, Boolean> skip) {
        this.iterator = iterator;
        this.skip = skip;
        moveOn();
    }

    private void moveOn() {
        while (iterator.hasNext()) {
            current = iterator.next();
            if (!skip.apply(current))
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
