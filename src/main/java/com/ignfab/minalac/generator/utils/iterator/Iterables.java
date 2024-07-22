package com.ignfab.minalac.generator.utils.iterator;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A utility class for operations on iterables.
 */
public final class Iterables {
    private Iterables() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an iterable over given array.
     *
     * @param array an array of items
     *
     * @return an iterable over items of provided array
     *
     * @param <T> type of items
     */
    public static <T> Iterable<T> array(T[] array) {
        return () -> Iterators.array(array);
    }

    /**
     * Filters result of an iterable according to a condition.
     * Only elements for which condition is true are returned.
     *
     * @param iterable iterable to filter
     * @param condition predicate telling which results to keep
     *
     * @return an iterable returning only results for which condition is true
     *
     * @param <T> type returned by iterable
     */
    public static <T> Iterable<T> filter(Iterable<T> iterable, Predicate<T> condition) {
        return () -> Iterators.filter(iterable.iterator(), condition);
    }

    /**
     * Creates an iterable over all items given as argument.
     * Using this method with only one argument is the right way to create a singleton iterable.
     *
     * @param items items iterable should return
     *
     * @return an iterable over items given as arguments
     *
     * @param <T> type of items
     */
    @SafeVarargs
    public static <T> Iterable<T> iterable(T... items) {
        return array(items);
    }

    /**
     * Remaps an iterable, transforming its result into another value using a
     * provided function.
     *
     * @param iterable the iterable to remap
     * @param mapper the mapping function
     *
     * @return an iterable returning transformed results
     *
     * @param <T> original type returned by iterable
     * @param <U> new type returned by resulting iterable
     */
    public static <T, U> Iterable<U> remap(Iterable<T> iterable, Function<T, U> mapper) {
        return () -> Iterators.remap(iterable.iterator(), mapper);
    }

    /**
     * Creates a singleton iterable over given item.
     *
     * @param item item returned by iterator
     *
     * @return an iterable returning only item given as argument
     *
     * @param <T> type of item
     */
    public static <T> Iterable<T> singleton(T item) {
        return () -> Iterators.singleton(item);
    }

    /**
     * Merges several iterables into one.
     *
     * @param iterables iterables to merge
     *
     * @return an iterable returning all results of iterables
     *
     * @param <T> common type returned by iterables to merge
     */
    @SafeVarargs
    public static <T> Iterable<T> union(Iterable<? extends T>... iterables) {
        return () -> Iterators.unwrap(Iterators.remap(Iterators.array(iterables), Iterable::iterator));
    }

     /**
     * Unwraps an iterable over iterables into an iterable over iterables results.
     *
     * @param iterable iterable over iterables
     *
     * @return an iterable returning all results of iterable iterables
     *
     * @param <T> common type returned by iterable iterables
     */
    public static <T> Iterable<T> unwrap(Iterable<? extends Iterable<? extends T>> iterable) {
        return () -> Iterators.unwrap(Iterators.remap(iterable.iterator(), Iterable::iterator));
    }

    /**
     * Unwraps an iterable over iterators into an iterable over iterators results.
     *
     * @param iterable iterable over iterators
     *
     * @return an iterable returning all results of iterable iterators
     *
     * @param <T> common type returned by iterables iterators
     */
    public static <T> Iterable<T> unwrapIterators(Iterable<? extends Iterator<? extends T>> iterable) {
        return () -> Iterators.unwrap(iterable.iterator());
    }

}

