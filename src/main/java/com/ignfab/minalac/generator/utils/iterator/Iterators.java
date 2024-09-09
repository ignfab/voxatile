package com.ignfab.minalac.generator.utils.iterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An utility class for iterators operations.
 */
public final class Iterators {
    private Iterators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an iterator oven given array.
     *
     * @param array an array of items
     *
     * @return an iterator over items of provided array
     *
     * @param <T> type of items
     */
    public static <T> Iterator<T> array(T[] array) {
        switch (array.length) {
            case 0:
                return Collections.emptyIterator();
            case 1:
                return new SingletonIterator<>(array[0]);
            default:
                return new ArrayIterator<>(array);
        }
    }

    /**
     * Casts an iterator over a type to an iterator over a super type.
     *
     * @param iterator iterator to cast
     *
     * @return an iterator with same results but casted to super type
     *
     * @param <From> type returned by iterator
     * @param <To> super type to cast results to
     */
    @SuppressWarnings("unchecked")
    public static <To, From extends To> Iterator<To> cast(Iterator<From> iterator) {
        return (Iterator<To>) iterator;
    }

    /**
     * Filters result of an iterator according to a condition.
     * Only elements for which condition is true are returned.
     *
     * @param iterator iterator to filter
     * @param condition predicate telling which results to keep
     *
     * @return an iterator returning only results for which condition is true
     *
     * @param <T> type returned by iterator
     */
    public static <T> Iterator<T> filter(Iterator<T> iterator, Predicate<T> condition) {
        return new FilterIterator<T>(iterator, condition);
    }

    /**
     * Creates an iterator oven all items given as argument.
     * Using this method with only one argument is the right way to create a singleton iterator.
     *
     * @param items items iterator should return
     *
     * @return an iterator over items given as arguments
     *
     * @param <T> type of items
     */
    @SafeVarargs
    public static <T> Iterator<T> iterator(T... items) {
        return array(items);
    }

    /**
     * Remap an iterator, transforming its result into another value using a
     * provided function.
     *
     * @param iterator the iterator to remap
     * @param mapper the mapping function
     *
     * @return an iterator returning transformed results
     *
     * @param <T> original type returned by iterator
     * @param <U> new type returned by resulting iterator
     */
    public static <T, U> Iterator<U> remap(Iterator<T> iterator, Function<T, U> mapper) {
        return new RemapIterator<T, U>(iterator, mapper);
    }

    /**
     * Merges several iterators into one.
     *
     * @param iterators iterators to merge
     *
     * @return an iterator returning all results of iterators
     *
     * @param <T> common type returned by iterators
     */
    @SafeVarargs
    public static <T> Iterator<T> union(Iterator<? extends T>... iterators) {
        return new UnwrapIterator<>(new ArrayIterator<>(iterators));
    }

    /**
     * Unwrap an iterator over iterators into an iterator over iterators results.
     *
     * @param iterator iterator over iterators
     *
     * @return an iterator returning all results of iterator iterators
     *
     * @param <T> common type returned by iterator iterators
     */
    public static <T> Iterator<T> unwrap(Iterator<? extends Iterator<? extends T>> iterator) {
        return new UnwrapIterator<>(iterator);
    }

    /**
     * Unwrap an iterator over iterables into an iterator over iterables results.
     *
     * @param iterator iterator over iterables
     *
     * @return an iterator returning all results of iterator iterables
     *
     * @param <T> common type returned by iterator iterables
     */
    public static <T> Iterator<T> unwrapIterables(Iterator<? extends Iterable<? extends T>> iterator) {
        return new UnwrapIterator<>(remap(iterator, Iterable::iterator));
    }

    /*
     * Inner classes
     */

    private static class ArrayIterator<T> implements Iterator<T> {
        private final T[] array;
        private int index;

        ArrayIterator(T[] array) {
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

    private static class FilterIterator<T> implements Iterator<T> {
        private final Iterator<T> iterator;
        private final Predicate<T> condition;
        private T current;

        FilterIterator(Iterator<T> iterator, Predicate<T> condition) {
            this.iterator = iterator;
            this.condition = condition;
            moveOn();
        }

        private void moveOn() {
            while (iterator.hasNext()) {
                current = iterator.next();
                if (condition.test(current))
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

    private static class RemapIterator<T, U> implements Iterator<U> {
        private final Iterator<T> iterator;
        private final Function<T, U> mapper;

        RemapIterator(Iterator<T> iterator, Function<T, U> mapper) {
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

    private static class SingletonIterator<T> implements Iterator<T> {
        private final T element;
        private boolean hasNext = true;

        SingletonIterator(T element) {
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

    private static class UnwrapIterator<T> implements Iterator<T> {
        private final Iterator<? extends Iterator<? extends T>> iterators;
        private Iterator<? extends T> current;

        UnwrapIterator(Iterator<? extends Iterator<? extends T>> iterators) {
            this.iterators = iterators;
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
}

